package com.zx.spark.core.log

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ListBuffer

//导入函数功能
import org.apache.spark.sql.functions._

/**
  * author:ZhengXing
  * datetime:2018-02-02 21:06
  * 第三步, topN统计
  *
  */
object TopNStatJob {


  def main(args: Array[String]): Unit = {
    //关闭了字段类型自动推导, 防止将day字段自动推到为int
    val spark = SparkSession.builder().appName("TopNStatJob").master("local[2]")
      .config("spark.sql.sources.partitionColumnTypeInference.enabled", "false").getOrCreate()

    //删除旧数据
    val day = "20161110"
    StatDao.deleteData(day)

    //最受欢迎的topn视频 使用DataFrame
    val resultDF = videoAccessTopNStat(spark,day)
    //最受欢迎的topn视频 使用sql
    //    val resultDF =  videoAccessTopNStat2(spark)

    //按照城市统计Topn课程
    val resultDF2 = videoAccessTopNStatByCity(spark,day)

    //按流量统计TopN课程
    val resultDF3 = videoAccessTopNStatByTraffic(spark,day)

    /**
      * 将TopN统计结果写入mysql
      * foreachPartition遍历每个分区的记录,每次循环获取到的是一个分区的所有记录
      */
    resultDF.foreachPartition(partitionList => {
      val list = new ListBuffer[DayVideoAccessStat]
      partitionList.foreach(item => {
        val day = item.getAs[String]("day")
        val cmsId = item.getAs[Long]("cmsId")
        val times = item.getAs[Long]("times")
        list.append(DayVideoAccessStat(day, cmsId, times))
      })
      StatDao.insertDayVideoAccessStat(list)
    })

    /**
      * 将根据城市统计TopN统计结果写入mysql
      * foreachPartition遍历每个分区的记录,每次循环获取到的是一个分区的所有记录
      */
    resultDF2.foreachPartition(partitionList => {
      val list = new ListBuffer[DayVideoCityAccessStat]
      partitionList.foreach(item => {
        val day = item.getAs[String]("day")
        val city = item.getAs[String]("city")
        val cmsId = item.getAs[Long]("cmsId")
        val times = item.getAs[Long]("times")
        val timesRank = item.getAs[Int]("timesRank")
        list.append(DayVideoCityAccessStat(day, city, cmsId, times, timesRank))
      })
      StatDao.insertDayVideoCityAccessStat(list)
    })

    /**
      * 根据流量统计TopN结果写入mysql
      * foreachPartition遍历每个分区的记录,每次循环获取到的是一个分区的所有记录
      */
    resultDF3.foreachPartition(partitionList => {
      val list = new ListBuffer[DayVideoStatByTraffic]
      partitionList.foreach(item => {
        val day = item.getAs[String]("day")
        val cmsId = item.getAs[Long]("cmsId")
        val trafficSum = item.getAs[Long]("trafficSum")
        list.append(DayVideoStatByTraffic(day, cmsId, trafficSum))
      })
      StatDao.insertDayVideoStatByTraffic(list)
    })

    spark.stop()
  }

  /**
    * 最受欢迎的topn视频, dataFrame方式
    */
  def videoAccessTopNStat(spark: SparkSession,day:String): DataFrame = {
    import spark.implicits._


    //读取上一步的数据
    val accessDF = spark.read.load("C:\\Users\\97038\\Desktop\\out2")
    accessDF.printSchema()
    accessDF.show(false)
    val videoAccessTopNDF = accessDF
      .filter($"day" === day && $"cmsType" === "video") //过滤出目标时间的,类型为视频的记录
      .groupBy("day", "cmsId") //根据天/视频id分组
      .agg(count("cmsId").as("times")) //统计每个id的记录数
      .orderBy($"times".desc) //根据该次数,倒序排序
    videoAccessTopNDF.show(false)

    videoAccessTopNDF
  }

  /**
    * 最受欢迎的topn视频, sql方式
    */
  def videoAccessTopNStat2(spark: SparkSession,day:String): DataFrame = {
    //读取上一步的数据
    spark.read.load("C:\\Users\\97038\\Desktop\\out2").createOrReplaceTempView("accessDF")
    val accessDF = spark.sql(s"select day,cmsId,count(1) as times from accessDF where day = '$day' and cmsType = 'video' group by day,cmsId order by times desc")
    accessDF.show(false)

    accessDF
  }


  /**
    * 按照城市统计Topn课程
    */
  def videoAccessTopNStatByCity(spark: SparkSession,day:String): DataFrame = {
    import spark.implicits._

    val accessDF = spark.read.load("C:\\Users\\97038\\Desktop\\out2")
    accessDF.printSchema()
    accessDF.show(false)

    val videoAccessTopNDF = accessDF
      .filter($"day" === day && $"cmsType" === "video") //过滤出目标时间的,类型为视频的记录
      .groupBy("day", "city", "cmsId") //根据天/视频id分组
      .agg(count("cmsId").as("times"))
    videoAccessTopNDF.show(false)

    //Window函数在Spark SQL中的使用
    val videoAccessTopNByCityDF = videoAccessTopNDF
      .select($"day", $"city", $"cmsId", $"times",
        row_number().over(Window.partitionBy($"city").orderBy($"times".desc)).as("timesRank")) //新增一个根据城市分组,然后根据times排序,显示排名的列
      .filter($"timesRank" <= 3) //每个城市最受欢迎的前3个课程

    videoAccessTopNByCityDF
  }

  /**
    * 根据流量统计TopN课程
    *
    * @param spark
    */
  def videoAccessTopNStatByTraffic(spark: SparkSession,day:String): DataFrame = {
    import spark.implicits._

    //读取上一步的数据
    val accessDF = spark.read.load("C:\\Users\\97038\\Desktop\\out2")
    accessDF.printSchema()
    accessDF.show(false)
    val videoAccessTopNDF = accessDF
      .filter($"day" === day && $"cmsType" === "video") //过滤出目标时间的,类型为视频的记录
      .groupBy("day", "cmsId") //根据天/视频id分组
      .agg(sum("traffic").as("trafficSum"))
      .orderBy($"trafficSum".desc)
    videoAccessTopNDF.show(false)

    videoAccessTopNDF
  }
}

package com.zx.spark.log

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * author:ZhengXing
  * datetime:2018-01-30 20:00
  * 使用spark进行数据清洗操作
  */
object StatCleanJob {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("StatCleanJob").master("local[2]").getOrCreate()

    //读取进行第一步清洗后的文件
    val accessRDD = spark.sparkContext.textFile("C:\\Users\\97038\\Desktop\\access.log")

    //RDD => DataFrame
    //将rdd转df,需要 将数据手动解析为 指定的schema格式
    //我增加了一步过滤操作
    val accessDF = spark.createDataFrame(accessRDD
      .filter(item => {
        val arr = item.split("\t")
        arr!= null && arr.size > 3 && arr(1).length > 10
      })
      .map(item => AccessConvertUtil.parseLog(item)),AccessConvertUtil.struct)
    accessDF.printSchema()
    accessDF.show(100,false)

    //保存解析结果,并根据day字段分区
    accessDF
      .coalesce(1)//指定输出的文件的个数
      .write.format("parquet")
        .mode(SaveMode.Overwrite)//指定文件保存模式,当有文件时,重写.
      .partitionBy("day").save("C:\\Users\\97038\\Desktop\\out2")

    spark.stop()
  }
}

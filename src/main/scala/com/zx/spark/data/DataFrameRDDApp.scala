package com.zx.spark.data

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
  * author:ZhengXing
  * datetime:2018-01-28 13:39
  * DataFrame 和 RDD 相互操作
  */
object DataFrameRDDApp {
  def main(args: Array[String]): Unit = {
    val sparkSession =SparkSession.builder().appName("DataFrameRDDApp").master("local[2]").getOrCreate()

    //RDD ==> DataFrame ,通过反射
//    convertByReflection(sparkSession)
    convertByProgram(sparkSession)


    sparkSession.stop()
  }

  /**
    * 通过编程将 RDD 转换为 DataFrame
    */
  private def convertByProgram(sparkSession: SparkSession): Unit = {
    //RDD ==> DataFrame
    //使用sparkContext将文件读取为RDD[String]
    val rdd = sparkSession.sparkContext.textFile("C:\\Users\\97038\\Desktop\\info.txt")

    //将每一行转换为一个Row对象,获取到一个RDD对象
    val infoRDD = rdd.map(row => row.split(",")).map(field => Row(field(0).toInt, field(1), field(2).toInt))

    //定义结构类型,需要传入一个数组,该数组每个元素是一个字段,每个字段需要传入 字段名/字段类型/是否能为空
    val structType = StructType(Array(
      StructField("id", IntegerType, true),
      StructField("name", StringType, true),
      StructField("age", IntegerType, true)))

    //通过 RDD对象和其对应的StructType对象 创建出 DataFrame
    val infoDF = sparkSession.createDataFrame(infoRDD, structType)
    infoDF.show()
  }

  /**
    * 通过反射将 RDD 转换为 DataFrame
    */
  private def convertByReflection(sparkSession: SparkSession): Unit = {
    //RDD ==> DataFrame
    //使用sparkContext将文件读取为RDD[String]
    val rdd = sparkSession.sparkContext.textFile("C:\\Users\\97038\\Desktop\\info.txt")

    //将rdd(分为若干行,每一行的记录用逗号分隔), 每一行都用逗号分隔(这样相当于每一行都被分割为三个字段),
    //然后再将这三个字段拼接为Info对象.
    //然后再将这个RDD转换为DataFrame(需要导入sparkSession.implicits._   (隐式转换))
    import sparkSession.implicits._
    val infoDF = rdd.map(row => row.split(","))
      .map(field => Info(field(0).toInt, field(1), field(2).toInt))
      .toDF()
    infoDF.show()

    //后续可以继续操作该DataFrame
    infoDF.filter(infoDF.col("age") > 30).show()

    //将其转换为临时表,然后可以直接用spark sql 处理
    infoDF.createOrReplaceTempView("infoTable")
    sparkSession.sql("select * from infoTable where age > 30").show()
  }

  //一个java bean,表示文件的schema
  case class Info(id: Int, name: String, age: Int)

}

package com.zx.spark.core.datasource

import org.apache.spark.sql.SparkSession

/**
  * author:ZhengXing
  * datetime:2018-01-28 16:19
  * 操作Parquet文件
  */
object ParquetApp {
  def main(args: Array[String]): Unit = {
    //创建SparkSession
    val spark = SparkSession.builder().appName("ParquetApp").master("local[2]").getOrCreate()
    //将parquet文件加载为DataFrame,如果不写format()方法,默认处理的格式为parquet
    val userDF = spark.read.format("parquet").load("file:///zx/spark-2.2.1-bin-hadoop2.7/examples/src/main/resources/users.parquet")
    userDF.printSchema()
    userDF.show()
    //只取其中的两个字段,保存为json文件,注意(这个save()方法指定的是一个目录,会将一些信息生成若干个文件都保存到该目录中)
    userDF.select("name","favorite_numbers").write.format("json").save("file:///jsonout")
    spark.stop()
  }
}

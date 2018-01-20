package com.zx.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SQLContext, SparkSession}

/**
  * author:ZhengXing
  * datetime:2018-01-20 17:26
  *
  */
object SparkSessionApp {
  def main(args: Array[String]): Unit = {
    //将运行时传入的第一个参数作为json文件的路径
    val path = args(0)
    //1. 创建
    val sparkSession = SparkSession.builder()
      .master("local[2]")
      .appName("SparkSessionApp")
      .getOrCreate()
    //2.读取json
    val people = sparkSession.read.json(path)
    //显示json
    people.show()
    //关闭
    sparkSession.stop()
  }
}

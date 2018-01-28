package com.zx.spark.context

import org.apache.spark.sql.SparkSession

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
    //2.读取json,返回值就是一个DataFrame
    val people = sparkSession.read.json(path)
    //或者如下写法,json()方法,不过是封装了下format("josn").load()这个语句
//    val people = sparkSession.read.format("josn").load()
    //显示json
    people.show()
    //关闭
    sparkSession.stop()
  }
}

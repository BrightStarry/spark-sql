package com.zx.spark.demo

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.SparkSession


/**
  * author:ZhengXing
  * datetime:2018-01-29 20:17
  * 统计 & 格式化 工作
  * 第一步清洗: 抽取出我们所需要的指定列的数据
  */
object StatFormatJob {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("StatFormatJob").master("local[2]").getOrCreate()
    //读取文件,为rdd
    val access = spark.sparkContext.textFile("C:\\Users\\97038\\Desktop\\10000_access.log")
    //读取文件为Dataset.如果这样做,还需要对齐编码.但此处我们不定义其schema
//    val access = spark.read.textFile("C:\\Users\\97038\\Desktop\\10000_access.log")
    //展示前10条
//    access.take(10).foreach(println)
    //分割每日志,解析出需要的字段,然后保存为文件
    access.map(line => {
      val splits = line.split(" ")
      val ip = splits(0)
      //原始日志的第三个字段和第四个字段拼接起来就是完整的时间: [10/Nov/2016:00:01:02 +0800]
      val time = DateUtil.parse(splits(3) + " " + splits(4))
      //获取url,并去除两侧多余的引号
      val url = splits(11).substring(1,splits(11).length-1)
      //流量
      val traffic = splits(9)
      time + "\t" + url + "\t" + traffic + "\t" + ip
    }).saveAsTextFile("C:\\Users\\97038\\Desktop\\out.log")

    spark.stop()
  }

}

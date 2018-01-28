package com.zx.spark.data

import org.apache.spark.sql.SparkSession

/**
  * author:ZhengXing
  * datetime:2018-01-28 15:39
  * Dataset 操作
  */
object DatasetApp {

  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder().appName("DatasetApp")
      .master("local[2]").getOrCreate()

    //需要导入隐式转换
    import sparkSession.implicits._

    //spark如何解析csv
    val df = sparkSession.read
      .option("header", "true") //解析头信息(也就是将第一行作为字段信息)
      .option("inferSchema", "true") //推断schema信息
        .csv("C:\\Users\\97038\\Desktop\\test.csv")
    df.show()
    //将DataFrame 转化为 Dataset
    val dataset = df.as[Test]
    //输出cid
    dataset.map(line => line.cid).show()

  }

  case class Test(id: Int, aid: Int, bid: Int, cid: Double)

}

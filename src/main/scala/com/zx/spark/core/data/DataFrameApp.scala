package com.zx.spark.core.data

import org.apache.spark.sql.SparkSession

/**
  * author:ZhengXing
  * datetime:2018-01-28 12:34
  * DataFrame API 基本操作
  */
object DataFrameApp {

  def main(args: Array[String]): Unit = {
    //创建SparkSession
    val sparkSession = SparkSession.builder().appName("DataFrameApp").master("local[2]").getOrCreate()
    //将json文件加载为DataFrame
    val testDataFrame = sparkSession.read.format("json").load("C:\\Users\\97038\\Desktop\\people.json")
    //打印出DataFrame的Schema(类似表信息)信息
    testDataFrame.printSchema()
    //输出文件内容(默认是前20条)
    testDataFrame.show()

    //查询某列所有的数据: select name from table
    testDataFrame.select("name").show()
    //除了传入列名字符串,还可以传入Column对象, 通过DataFrame的col()方法返回Column对象
    //并显示出+10后的age字段  : select name. age + 10 from table
    //并且可以给计算后显示的列名起别名(如果不起,列名是  age + 10 )
    testDataFrame.select(testDataFrame.col("name"),(testDataFrame.col("age") + 10).as("age2")).show()

    //根据某一列的值进行过滤 : select * from table where age > 19(他也有DataFrame.where()方法)
    testDataFrame.filter(testDataFrame.col("age") > 19).show()

    //根据某一列进行分组,再进行聚合操作 : select age,count(1) from table group by age (也就是计算每个年龄有多少人)
    testDataFrame.groupBy("age").count().show()

    sparkSession.stop()
  }


}

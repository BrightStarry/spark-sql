package com.zx.spark.core.datasource

import org.apache.spark.sql.SparkSession

/**
  * author:ZhengXing
  * datetime:2018-01-28 17:10
  * 使用外部数据源
  * 将hive和mysql的表进行关联查询
  */
object HiveMySqlApp {
  def main(args: Array[String]): Unit = {
    //创建SparkSession
    val spark = SparkSession.builder().appName("HiveMySqlApp").master("local[2]").getOrCreate()
    //加载hive表数据
    val hiveDF = spark.table("temp")
    //加载mysql表的数据
    val jdbcDF = spark.read.format("jdbc").option("url", "jdbc:mysql://localhost:3306/hive").option("dbtable", "hive.TBLS").option("user", "root").option("password", "123456").option("driver", "com.mysql.jdbc.Driver").load()

    //join操作
    val resultDF =  hiveDF.join(jdbcDF, hiveDF("xx") === jdbcDF("xxx"))
    resultDF.show()
    //然后可以继续写查询代码
//    resultDF.select(xxxxxxxx)

    spark.stop()
  }
}

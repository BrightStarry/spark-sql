package com.zx.spark.context

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * author:ZhengXing
  * datetime:2018-01-20 14:43
  * sqlContext的使用
  */
object SQLContextApp {

  def main(args: Array[String]): Unit = {
    //将运行时传入的第一个参数作为json文件的路径
    val path = args(0)

    //1.创建相应的SQLContext,使用过时的方法
    //spark参数配置
    val sparkConf = new SparkConf()
    //在服务器中测试或生产环境,appName和Master是通过脚本指定的.
    //本地运行时,可通过如下代码指定
//    sparkConf.setAppName("SQLContextAPP").setMaster("local[2]")
    //构建sparkContext
    val sc = new SparkContext(sparkConf)
    //构建SQLContext
    val sqlContext = new SQLContext(sc)
    //2.相关处理:json
    //从传入的路径中读取json文件,val people的类型为DataFrame
    val people = sqlContext.read.format("json").load(path)
    //输出这个json解析的模式,类似于表结构
    people.printSchema()
    //输出json数据,类似于表数据
    people.show()
    //3.关闭资源
    sc.stop()
  }
}

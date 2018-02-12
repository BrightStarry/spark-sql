package com.zx.spark.core.context

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * author:ZhengXing
  * datetime:2018-01-20 16:02
  * HiveContext的使用
  */
object HiveContextApp {
  def main(args: Array[String]): Unit = {
    //1.创建相应的HiveContextApp,使用过时的方法
    //spark参数配置
    val sparkConf = new SparkConf()
    //在服务器中测试或生产环境,appName和Master是通过脚本指定的.
    //本地运行时,可通过如下代码指定
    //    sparkConf.setAppName("HiveContextApp").setMaster("local[2]")
    //构建sparkContext
    val sc = new SparkContext(sparkConf)
    //构建HiveContext
    val hiveContext = new HiveContext(sc)
    //2.相关处理:
    //显示相关表
    hiveContext.sql("show tables").show()
    //3.关闭资源
    sc.stop()
  }
}

package com.zx.spark.log

import java.sql.{Connection, DriverManager, PreparedStatement}

/**
  * author:ZhengXing
  * datetime:2018-02-02 21:35
  * mysql操作工具类
  */
object MySQLUtil {

  /**
    * 获取数据库连接
    */
  def getConnection() = {
    DriverManager.getConnection("jdbc:mysql://localhost:3306/spark-demo?user=root&password=123456&serverTimezone=UTC")
  }

  /**
    * 释放资源
    */
  def release(connection:Connection,pstmt: PreparedStatement):Unit={
    try {
      if (pstmt != null)
        pstmt.close()
    } finally{
      if(connection != null) {
        connection.close()
      }
    }
  }

  def main(args: Array[String]): Unit = {
    println(getConnection())
  }

}

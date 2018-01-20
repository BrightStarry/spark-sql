package com.zx.spark

import java.sql.DriverManager

import org.apache.hive.jdbc.HiveDriver

/**
  * author:ZhengXing
  * datetime:2018-01-20 19:04
  * JDBC连接到thriftserver访问SparkSQL
  */
object SparkSQLThriftServerApp {
  def main(args: Array[String]): Unit = {
    Class.forName("org.apache.hive.jdbc.HiveDriver")
    val connection = DriverManager.getConnection("jdbc:hive2://106.14.7.29:10000","root","")
    val pstmt = connection.prepareStatement("select * from emp")
    val rs = pstmt.executeQuery()
    while(rs.next()) {
      println("id:" + rs.getString("id") + "----" + "name:" + rs.getString("name") )
    }
    rs.close()
    pstmt.close()
    connection.close()
  }
}

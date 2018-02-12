package com.zx.spark.core.log

import java.sql.{Connection, PreparedStatement}

import scala.collection.mutable.ListBuffer

/**
  * author:ZhengXing
  * datetime:2018-02-03 15:33
  * 统计 dao操作
  */
object StatDao {

  /**
    * 批量保存DayVideoAccessStat到数据库
    *
    * @param list
    */
  def insertDayVideoAccessStat(list: ListBuffer[DayVideoAccessStat]): Unit = {
    var connection: Connection = null
    var pstmt: PreparedStatement = null

    try {
      connection = MySQLUtil.getConnection()

      //设为手动提交
      connection.setAutoCommit(false)

      val sql = "insert into day_video_access_topn_stat(day,cms_id,times)  values(?,?,?)"
      pstmt = connection.prepareStatement(sql)

      for (item <- list) {
        pstmt.setString(1, item.day)
        pstmt.setLong(2, item.cmsId)
        pstmt.setLong(3, item.times)
        pstmt.addBatch()
      }
      pstmt.executeBatch()
      //手工提交
      connection.commit()
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      MySQLUtil.release(connection, pstmt)
    }
  }


  /**
    * 批量保存DayVideoCityAccessStat到数据库
    *
    * @param list
    */
  def insertDayVideoCityAccessStat(list: ListBuffer[DayVideoCityAccessStat]): Unit = {
    var connection: Connection = null
    var pstmt: PreparedStatement = null

    try {
      connection = MySQLUtil.getConnection()

      //设为手动提交
      connection.setAutoCommit(false)

      val sql = "insert into day_video_city_access_topn_stat(day,city,cms_id,times,times_rank)  values(?,?,?,?,?)"
      pstmt = connection.prepareStatement(sql)

      for (item <- list) {
        pstmt.setString(1, item.day)
        pstmt.setString(2, item.city)
        pstmt.setLong(3, item.cmsId)
        pstmt.setLong(4, item.times)
        pstmt.setInt(5, item.timesRank)
        pstmt.addBatch()
      }
      pstmt.executeBatch()
      //手工提交
      connection.commit()
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      MySQLUtil.release(connection, pstmt)
    }
  }

  /**
    * 批量保存DayVideoStatByTraffic到数据库
    *
    * @param list
    */
  def insertDayVideoStatByTraffic(list: ListBuffer[DayVideoStatByTraffic]): Unit = {
    var connection: Connection = null
    var pstmt: PreparedStatement = null

    try {
      connection = MySQLUtil.getConnection()

      //设为手动提交
      connection.setAutoCommit(false)

      val sql = "insert into day_video_traffic_access_topn_stat(day,cms_id,traffic_sum)  values(?,?,?)"
      pstmt = connection.prepareStatement(sql)

      for (item <- list) {
        pstmt.setString(1, item.day)
        pstmt.setLong(2, item.cmsId)
        pstmt.setLong(3, item.trafficSum)
        pstmt.addBatch()
      }
      pstmt.executeBatch()
      //手工提交
      connection.commit()
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      MySQLUtil.release(connection, pstmt)
    }
  }

  /**
    * 删除统计表指定日期的所有记录
    */
  def deleteData(day: String): Unit = {
    val tables = Array[String]("day_video_access_topn_stat",
      "day_video_city_access_topn_stat",
      "day_video_traffic_access_topn_stat")
    var connection:Connection = null
    var pstmt:PreparedStatement = null

    try{
      connection = MySQLUtil.getConnection()
      for (table <- tables) {
        val sql = s"delete from $table where day = ?"
        pstmt = connection.prepareStatement(sql)
        pstmt.setString(1,day)
        pstmt.executeUpdate()
      }

    }catch{
      case e:Exception => e.printStackTrace()
    }finally {
      MySQLUtil.release(connection,pstmt)
    }
  }

}

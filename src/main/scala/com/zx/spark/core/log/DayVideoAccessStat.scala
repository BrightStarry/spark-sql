package com.zx.spark.core.log

/**
  * 每天视频访问统计 实体类
  */
case class DayVideoAccessStat(day: String, cmsId: Long, times: Long)

/**
  * 根据城市统计TopN
  */
case class DayVideoCityAccessStat(day: String, city: String, cmsId: Long, times: Long, timesRank: Int)

/**
  * 根据流量统计TopN
  */
case class DayVideoStatByTraffic(day: String, cmsId: Long, trafficSum: Long)

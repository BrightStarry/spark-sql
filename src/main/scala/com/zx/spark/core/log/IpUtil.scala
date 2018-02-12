package com.zx.spark.core.log

import com.ggstar.util.ip.IpHelper

/**
  * author:ZhengXing
  * datetime:2018-01-30 20:58
  * ip解析工具类
  */
object IpUtil {
  def getCity(ip:String) :String = {
    IpHelper.findRegionByIp(ip)
  }

  def main(args: Array[String]): Unit = {
    println(getCity("10.100.0.1"))
  }
}

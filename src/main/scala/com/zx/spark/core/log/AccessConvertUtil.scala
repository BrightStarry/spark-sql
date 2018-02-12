package com.zx.spark.core.log

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

/**
  * author:ZhengXing
  * datetime:2018-01-30 20:03
  * 访问日志转换工具类
  * 输入 == > 输出
  */
object AccessConvertUtil {
  //定义输出字段
  val struct = StructType(
    Array(
      StructField("url",StringType),//url
      StructField("cmsType",StringType),//是视频还是手记
      StructField("cmsId",LongType),//视频或手记ip
      StructField("traffic",LongType),//流量
      StructField("ip",StringType),//ip
      StructField("city",StringType),//城市
      StructField("time",StringType),//时间
      StructField("day",StringType)//分区字段,根据天分区
    )
  )

  /**
    * 将每行输入的log转换为输出的格式
    */
  def parseLog(log: String): Row = {
    try {
      val arr = log.split("\t")

      val url = arr(1)
      val traffic = arr(2).toLong
      val ip = arr(3)
      //固定的域名
      val domain = "http://www.imooc.com/"
      //截取域名后的路径字符
      val cms = url.substring(url.indexOf(domain) + domain.length)
      //将路径按"/"分割
      val types = cms.split("/")

      //根据路径后缀截取出类型(课程或手记),以及对应id, 例如: /video/8701
      var cmsType = ""
      var cmsId = 0L
      if (types.length > 1) {
        cmsType = types(0)
        cmsId = types(1).toLong
      }

      val city = IpUtil.getCity(ip)
      val time = arr(0)
      //截取出time中的日期,并去除-, 例如 2016-11-10 00:01:02 => 20161110
      val day = time.substring(0, 10).replaceAll("-", "")

      //这个Row需要和Struct中的字段对应
      Row(url, cmsType, cmsId, traffic, ip, city, time, day)
    } catch {

      //错误时返回0
      case e:Exception => {println("error")
        Row("","",0L,0L,"","","","")}
    }
  }

}

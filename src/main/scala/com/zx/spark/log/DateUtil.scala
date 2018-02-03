package com.zx.spark.log

import java.util.{Date, Locale}

import org.apache.commons.lang3.time.FastDateFormat

/**
  * author:ZhengXing
  * datetime:2018-01-29 20:34
  * 日期工具类
  *
  * 注意,之所以使用FastDateFormat,而不使用SimpleDateFormat,是因为后者是线程不安全的.
  */
object DateUtil {

  //输入的日期格式: [10/Nov/2016:00:01:02 +0800]
  val INPUT_FORMAT = FastDateFormat.getInstance("[dd/MMM/yyyy:HH:mm:ss Z]",Locale.ENGLISH)

  //目标日期格式
  val TARGET_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")

  /**
    * 解析
    */
  def parse(time:String) = {
    //此处直接在日期格式外增加了"[]",教程中是用subString去除了[]的
    TARGET_FORMAT.format(new Date(getTime(time)))
  }

  /**
    * 输入日期转为long
    */
  def getTime(time:String) ={
    try {
      INPUT_FORMAT.parse(time).getTime
    } catch {
      //发生异常时直接返回0,表示解析失败
      case  e: Exception =>{
        0L
      }
    }
  }

  def main(args: Array[String]): Unit = {
    println(parse("[10/Nov/2016:00:01:02 +0800]"))
  }
}

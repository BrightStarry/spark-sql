package com.zx.spark.core.data

import org.apache.spark.sql.SparkSession

/**
  * author:ZhengXing
  * datetime:2018-01-28 14:30
  * DataFrame中的其他操作
  *
  */
object DataFrameCase {

  def main(args: Array[String]): Unit = {
    //创建SparkSession
    val sparkSession = SparkSession.builder().appName("DataFrameCase").master("local[1]").getOrCreate()
    //读取出rdd类型对象
    val rdd = sparkSession.sparkContext.textFile("C:\\Users\\97038\\Desktop\\student.data")

    /**
      * 转为DataFrame
      * 我之所以输出了每次map的中间值,是因为遇到一个 空字符串无法toInt的bug.
      * 最后发现是我的student.data文件底下有几个空行,导致的.这个bug...把我自己的逗笑了..难受..
      */
    import sparkSession.implicits._
    val studentDF = rdd
      .map(row => {
//        println(row)
        row.split("\\|")})
      .map(field => {
//        println(field(0))
        Student(field(0).toInt,field(1),field(2),field(3))
      }).toDF()

    //显示.指定条数,默认20, 第二个boolean参数表示是否截去超出长度20的字符(显示为xxx...)
    studentDF.show(30,false)
    //获取(注意是获取)前10行,并循环打印(内部是调用head()方法)
    studentDF.take(10).foreach(println(_))
    //第一行记录(内部是调用head()方法)
    println(studentDF.first())
    //获取前10行记录
    studentDF.head(10).foreach(println(_))

    //过滤出名字为空字符或者null字符的
    studentDF.filter("name='' OR name='NULL'").show()

    //过滤出名字以O开头的
    studentDF.filter("SUBSTRING(name,0,1)='O'").show()

    //可通过如下sql查询出所有内置函数, (其第一位应该为符号位,如果直接用Integer.MAX_VALUE,会提示超出最小值)
//    sparkSession.sql("show functions").show(Integer.MAX_VALUE-1)

    //排序(studentDF("name") 等价于 studentDF.col("name"))
    studentDF.sort(studentDF("name").desc).show()
    //按照名字升序,如果名字相同,则按照id降序排序
    studentDF.sort(studentDF("name").asc,studentDF("id").desc).show()


    //再创建一个相同的DataFrame
    val studentDF2 = rdd
      .map(row => {
        //        println(row)
        row.split("\\|")})
      .map(field => {
        //        println(field(0))
        Student(field(0).toInt,field(1),field(2),field(3))
      }).toDF()

    //join查询操作. 第一个参数为要join的Dataset(DataFrame属于Dataset).第二个参数为join的表达式, 注意需要使用===,默认是inner join
    studentDF.join(studentDF2, studentDF("id") === studentDF2("id")).show()

    sparkSession.stop()
  }

  //定义bean
  case class Student(id: Int,name: String, phone: String, email: String)

}

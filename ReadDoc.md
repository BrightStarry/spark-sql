#### 阅读官方文档

* 并行集合  
    * 如下方式创建spark并行集合
    >
        val data = Array(1, 2, 3, 4, 5)
        val distData = sc.parallelize(data)
    >
    * 并行集合有一个重要参数是将该集合进行分区的数量,spark会为每一个分区运行一个任务.
    * 通常情况下,每个cpu可分配2-4个分区.
    * 可自行传入参数指定分区数量,如下指定了10个分区(若不指定,则由spark自动分配)
    >   sc.parallelize(data, 10)

* spark读取文件的注意事项
    * 如果读取本地文件,则所有节点都需要可以在各自的本地访问到该文件.
    * spark所有基于文件的输入方法,支持目录/压缩文件和通配符.例如下面这些读取方式
    > textFile("/my/directory")，textFile("/my/directory/*.txt")和textFile("/my/directory/*.gz")
    * textFile()方法也可用参数[2]来控制分区数量.默认情况下,spark为文件的每个块创建一个分区
        (HDFS中,块默认为128M(注意,分区数不能小于块数目,也就是说分区数之内能比默认分区数大)).
    * SparkContext.wholeTextFiles()方法可以读取多个小文本文件的目录,将它作为scala中的  (filename, content) 对象返回.
        某些时候这样会导致分区数太少,所以该方法的参数[2]可以指定最小分区数量        
    * RDD.saveAsObjectFile()和SparkContext.objectFile()方法可以用由序列化java对象组成的简单格式,保存RDD    
* spark中,所有转换都是懒惰的.只会保存一些基础的Dataset(例如文件).只有当驱动程序需要结果时,才会进行计算(例如文件).    
* 默认情况下,每次转换RDD时,都会重新计算(例如执行相同的filter,都会重新计算).可以使用persist()或cache()方法保存中间结果.  
    此外,还支持在本地持久化,或在集群中复制.     
    >
        如下代码.在第一行和第二行,读取文件时,spark不会马上进行计算,当最后reduce()方法时(这是一个行动),才会进行计算.
        val lines = sc.textFile("data.txt")
        val lineLengths = lines.map(s => s.length)
        val totalLength = lineLengths.reduce((a, b) => a + b)
        
        如果我们想在之后再次使用lineLengths对象,可以在reduce之前,使用lineLengths.persist(),将其保存在内存中
    >
* scala中,可以如下定义静态方法,然后可以在map()时调用; (和Java8中的方法引用类似)
    >
        object MyFunctions {
          def func1(s: String): String = { ... }
        }
        
        //调用
        myRdd.map(MyFunctions.func1)
    >
* 也可以将rdd对象传递给要调用的方法所属的类,进行引用
    >
        如下,doStuff方法接收任意rdd对象,在rdd.map()中调用自己的func1方法
        class MyClass {
          def func1(s: String): String = { ... }
          def doStuff(rdd: RDD[String]): RDD[String] = { rdd.map(func1) }
        }
    >

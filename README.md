#### Spark
* 分布式的基于内存的列式存储计算框架
* MapReduce局限性
    1. 代码繁琐
    2. 只支持map和reduce方法,
    3. 效率低
    4. 不适合迭代多次/交互式/流式处理
* 框架多样化：会导致学习/运维成本都提高
    1. 批处理（离线）：MapReduce、Hive、Pig
    2. 流式处理（实时）： Storm、JStorm
    3. 交互式计算：Impala
    
* Spark就能完成上面这些所能做的事
#### 注意点
* mvn命令中 -D**的参数是指pom.xml中properties中的参数, -P**的参数是指pom.xml中profile中的参数
* 如果mvn命令提示仓库中已有xxx包,可以使用-U命令
* mvn -X,查看到更详细的错误信息
* jps -m 查看更详细的信息

#### bug-部分执行时遇到的问题写在了笔记中
* linux中tar命令提示错误,可能是下载时,下载的链接不是最终连接,例如点进去还有一个目录,但你wget了前一个目录.

* 执行spark编译,或者命令行时,莫名出现killed等情况,通常是内存不够



#### 编译
* 自行编译源码 或跳过这一步,直接下载编译好的(主要为了和CDH的hadoop适配,spark的包中需要绑定所使用的hadoop)
    * 在官网选择版本,并选择Source code下载,解压.
    * 查看官网的文档可得,当前版本(2.2.1)需要maven3.3.9以及java8+.
    * 需要配置maven使用内存数的参数(如果使用内置mvn,会自动添加,并且spark在测试编译时,也都会自动添加,所以通常至少要2g内存.)
        > export MAVEN_OPTS="-Xmx2g -XX:ReservedCodeCacheSize=512m"
    * 编译,并自定义scala版本,hadoop版本,yarn版本(如果yarn版本和hadoop版本不同,可用-Dyarn.version指定),hive支持    
        > ./build/mvn -Pyarn  -Phadoop-2.6 -Dhadoop.version=2.6.0-cdh5.13.0  -Phive -Phive-thriftserver  -DskipTests -X clean package
    * 如上编译不能直接使用,可用如下命令编译出可直接使用的包.其本质还是shell脚本调用mvn命令
        > ./dev/make-distribution.sh \
        --name 2.6.0-cdh5.13.0  --tgz \
        -Dscala-2.12.4 -Pyarn  -Phadoop-2.6 -Dhadoop.version=2.6.0-cdh5.13.0 -X  \
        -Phive -Phive-thriftserver  
        * 如上命令其实就--tgz后面跟的还是一样的mvn参数.
        * --name xxx 该参数指定编译后的文件名,在编译完成后,则包名为 spark-2.2.1-bin-xxx.taz
        * 编译cdh版本的hadoop.需要在spark的pom.xml中增加如下仓库
            >
                <repository>
                    <id>cloudera</id>
                    <url>https://repository.cloudera.com/artifactory/cloudera-repos/</url>
                </repository>
            >
        * 有问题,可尝试在pom.xml注释
            >  <!--<useZincServer>true</useZincServer>-->
        * 目前我的编译卡在了内存处,上面的Xmx2g小于2g,各种中断,等于2g,就报内存不足.只得作罢

#### 安装
* 安装scala和maven以及spark
解压,配置环境变量即可.(直接在阿里云服务器上wget scala的解压包..速度比本地翻墙快多了)  
分别运行scala 和 mvn -version测试.

* 可运行spark_home/bin/spark-shell --master local[2] 启动spark.进入命令行界面    
--master指定运行模式,local表示运行模式是本地,其他可选项有yarn/standalone(自带的)等  
[2]表示开启2个线程,[*]表示使用机器上所有线程

* 可进入http://106.14.7.29:4040 查看管理界面

* 运行一个word count
>
    读取文件内容到file变量
    val file = sc.textFile("file:///zx4/Dockerfile")
    将file读取到的文件转为Array[String]
    file.collect
    统计行数
    file.count
    将文件内容按照 空格 分割
    val a = file.flatMap(line => line.split(" "))
    将a转为Array[String]输出
    a.collect
    将a的每个元素附上1
    val b = a.map(word => (word,1))
    输出b
    b.collect
    将所有相同单词两两相加
    val c = b.reduceByKey(_ + _)
    输出c,就得到了单词统计的结果
    c.collect
>
* 上面的真正代码是(运行时不要分行)
>
    sc.textFile("file:///zx4/Dockerfile").flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey(_ + _).collect
>
        
#### Standalone模式
* 使用spark自带的Standalone模式启动(具体查看官方文档)
    * Spark Standalone模式的架构和Hadoop HDFS/YARN类似
        > 一个Master + n个worker
    * 文件配置
        >
            将conf/spark-env.sh.template 复制为 spark-env.sh
            参考其注释的说明,增加如下参数
                SPARK_MASTER_HOST=hadoop000  #主机名
                SPARK_WORKER_CORES=1  # 允许spark应用使用的机器cpu内核总数,默认全部
                SPARK_WORKER_MEMORY=512m # 分配多少内存
                export JAVA_HOME=/zx/jdk1.8.0_151
            
            如果是分布式配置,还需要拷贝出slaves.template文件为 slaves文件.
            然后将所有worker的hoatname都配置上去,然后在master服务器中启动start-all.sh命令时,就可以自动启动所有worker服务器.
        >
    * 启动集群
        >
             sbin/start-all.sh
             通过查看master的日志可知:
                sparkMaster:7077端口
                MasterUI:8080端口
                REST server:6066端口
                并在Master中注册了一个worker
             查看worker日志可知:
                sparkWorker:33852端口
                WorkerUI:8081端口
                并通过7077端口连接到了master
                
        >
    * 运行spark-shell
        >   
            spark-shell  --total-executor-cores 1   --master spark://hadoop000:7077  --driver-memory 512m --executor-memory 512m
            (很重要)通过sharp-shell --help查看所有参数
            --master指定启动模式为Standalone的话,直接在后面加spark://xxx,表示master节点的url即可.
            --driver-memory指定驱动内存(默认为1g),--executor-memory指定执行器内存(默认为1000m),
                之前就因为这两个参数导致core一直为0,一直为等待状态,无法启动.
            --total-executor-cores(或--executor-cores)配置该应用的核心数.
            例如集群总的核心数为2,默认启用后,使用所有核心数;那么只能启动一个spark-shell,再启动一个的话,第二个获取的核心数为0,状态为waitting,将无法使用
            
            启动后查看8080端口(master的管理界面),可查看到当前所有的运行应用,也就是这个spark-shell
        >
    * 运行上面local模式时测试使用的单词统计.
        >sc.textFile("file:///zx4/Dockerfile").flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey(_ + _).collect
        
* 任何启动模式都和代码无关,开发时,可直接使用local模式.

#### Spark SQL
* 官网:http://spark.apache.org/sql/
* Shark: Spark推出后,很受欢迎,于是有了Hive on Spark(shark):其目的是为了让Hive基于Spark运行,计算.
    * 缺点: hive ql解析/逻辑执行计划生成/执行计划的优化还是依赖于hive的,只是把物理执行计划从mapReduce替换为了Spark
* Shark终止后,有了两个分支
    * Hive on spark:为了继续支持旧的Shark,Hive社区的
    * Spark SQL:Spark社区的,支持多种数据源,多种优化技术,扩展性也要好很多
    
* SQL on Hadoop的各类框架
    1. Hive 
        >
                sql ==> mapreduce   
                metastore ： 元数据   
                sql：database、table、view  
                facebook提供的开源的  
        >
    2. impala
        >
                cloudera ： cdh（建议大家在生产上使用的hadoop系列版本）、cm(图形化界面操作)  
                sql：自己的守护进程执行的，非mr  
                metastore     
        > 
    3. presto  
        > facebook   京东 sql
    4. drill  
        > sql 访问：hdfs、rdbms、json、hbase、mongodb、s3、hive  
    5. Spark SQL  
        >
                sql  
                dataframe/dataset api  
                metastore  
                访问：hdfs、rdbms、json、hbase、mongodb、s3、hive  ==> 外部数据源
        >  

* Spark SQL概述小结：
    1. Spark SQL的应用并不局限于SQL；
    2. 访问hive、json、parquet等文件的数据；
    3. SQL只是Spark SQL的一个功能而已；
    4. Spark SQL提供了SQL的api、DataFrame和Dataset的API    
* Spark SQL愿景
    1. 写更少的代码
    2. 读更少的数据,例如不读取无关数据等
    3. 无需关注优化   


    
#### IDEA 构建Scala项目
* 使用IDEA创建maven项目,选择骨架为scala-archetype-simple
    >
        只需如下依赖,并修改其版本(可删除其他关于插件/仓库等所有无关信息.)
            <dependency>
              <groupId>org.scala-lang</groupId>
              <artifactId>scala-library</artifactId>
              <version>${scala.version}</version>
            </dependency>
            <dependency>
              <groupId>org.apache.spark</groupId>
              <artifactId>spark-sql_2.11</artifactId>
              <version>${spark-sql.version}</version>
            </dependency>
        还需保留下面的一系列编译插件,最好将其版本改为jvm1.8
        如果下面的运行出错,尝试修改scala.version,不用一定和本地安装的scala环境版本相同.
        我本地环境的scala为2.12.4,使用scala-library版本相同时,报ClassNotFound,更换为2.11.8才可以.
    >
* 或者这样创建
    * 使用idea创建scala - spark项目步骤.(我当前版本的idea/scala构建sbt项目出错,版本不兼容.可能要等更新)
    ![](img/1.png)
    * 右击项目 -> Add Framework Support ,增加maven,然后配置如下
    >
        <properties>
            <maven.test.skip>true</maven.test.skip>
            <spark.version>2.2.1</spark.version>
            <scala.version>2.12.4</scala.version>
        </properties>
    
        <dependencies>
            <dependency>
                <groupId>org.apache.spark</groupId>
                <artifactId>spark-core_2.11</artifactId>
                <version>${spark.version}</version>
            </dependency>
        </dependencies>
    >
    * 注意,创建scala的object,是选择创建scala class的过程中,和选择java接口一样,选择这个类型的.
    * 缺少hadoop的winutils.exe,自行下载放入 
    * 单次统计类
    >
        object WordCount {
          def main(args: Array[String]): Unit ={
            //应用名
            val sparkConf = new SparkConf().setAppName("wordCount")
            val sparkContext = new SparkContext(sparkConf)
        
            val input = sparkContext.textFile("D://a.txt")
        
            val result = input.flatMap(_.split(" "))
              .map((_,1))
              .reduceByKey(_ + _)
            result.saveAsTextFile("D://b")
          }
        }
    >
    
### 从Hive过度到Spark SQL
* SQLContext/HiveContext/SparkContext的使用
    * SQLContext/HiveContext是Spark1.x中的类.已经废弃,只做了解

#### SQLContext(可以在1.6老版本的文档中查看)
* Spark1.x中SparkSQL的入口点:SQLContext
>
    val sc: SparkContext // 一个存在的SparkContext
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
>
    
* 编写SQLContext的scala类
>
    object SQLContextAPP {
    
      def main(args: Array[String]): Unit = {
        //将运行时传入的第一个参数作为json文件的路径
        val path = args(0)
    
        //1.创建相应的SQLContext,使用过时的方法
        //spark参数配置
        val sparkConf = new SparkConf()
        //在服务器中测试或生产环境,appName和Master是通过脚本指定的.
        //本地运行时,可通过如下代码指定
    //    sparkConf.setAppName("SQLContextAPP").setMaster("local[2]")
        //构建sparkContext
        val sc = new SparkContext(sparkConf)
        //构建SQLContext
        val sqlContext = new SQLContext(sc)
        //2.相关处理:json
        //从传入的路径中读取json文件,val people的类型为DataFrame
        val people = sqlContext.read.format("json").load(path)
        //输出这个json解析的模式,类似于表结构
        people.printSchema()
        //输出json数据,类似于表数据
        people.show()
        //3.关闭资源
        sc.stop()
      }
    }
>
* 本地测试
    * edit run配置,传入参数为json文件的路径
    > C:\Users\97038\Desktop\people.json
    * 必须设置运行模式: A master URL must be set in your configuration. 在执行的命令后携带VM参数-Dspark.master=local   
        也可以在sparkConf类中增加 sparkConf.setAppName("SQLContextAPP").setMaster("local[2]")
* 服务器上测试: 编译后上传jar到服务器,集群运行
    * 提交Spark Application到环境中运行,命令如下
        >
            ./bin/spark-submit \
              --name <appName> \
              --class <main-class> \
              --master <master-url> \
              --deploy-mode <deploy-mode> \
              --conf <key>=<value> \
              ... # other options
              <application-jar> \  # 要提交的jar的位置
              [application-arguments] # 要传递的参数
              
            我们可以据此.设置自己的命令
            ./bin/spark-submit \
                --name SQLContextAPP \
                --class com.zx.spark.SQLContextAPP \
                --master local[2] \
                /zx/spark-2.2.1-bin-hadoop2.7/spark-sql-1.0.jar \
                /zx/spark-2.2.1-bin-hadoop2.7/examples/src/main/resources/people.json 
            
            工作中通常使用shell运行,将其拷贝到一个xxx.shell中,
            然后使用 chmod u+x xxx.shell 给与其执行权限,运行即可
        >

#### HiveContext的使用
* spark1.x中SparkSQL的入口点:HIveContext
    >
        //sc is an existing sparkContext
        val hiveContext = new org.apache.spark.sql.hive.HiveContext(sc)
    >
* 使用hiveContext并不需要有一个安装好的hive环境,只需要一个hive-site.xml即可

* 代码
    * 在之前的项目中增加依赖
        >
                <dependency>
                  <groupId>org.apache.spark</groupId>
                  <artifactId>spark-hive_2.11</artifactId>
                  <version>${spark.version}</version>
                </dependency>
        >
    * HiveContextApp类
        >
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
            hiveContext.table("user").show()
            //3.关闭资源
            sc.stop()
        >
* 服务器上测试: 同上
    * 运行
        >
            ./bin/spark-submit \
                --name HiveContextAPP \
                --class com.zx.spark.HiveContextApp \
                --master local[2] \
                --jars /zx/hive/lib/mysql-connector-java-5.1.36.jar \
                /zx/spark-2.2.1-bin-hadoop2.7/spark-sql-1.0.jar 
                
            需要指定hive元数据存储数据库的驱动,此处是mysql的驱动的路径(--jars /zx/hive/lib/mysql-connector-java-5.1.36.jar)
            需要将hive-site.xml拷贝到spark_home/conf/目录下,否则,即使在hive中创建了表,在spark执行时,也无法找到该表  
        >

#### SparkSession的使用
* Spark2.x中Spark SQL的入口点
    >
        val spark = SparkSession
            .builder()
            .appName("SparkSessionApp")
            .config("spark.some.config.option","some-value")
            .getOrCreate()
    >
* 代码
    >
         //将运行时传入的第一个参数作为json文件的路径
            val path = args(0)
            //1. 创建
            val sparkSession = SparkSession.builder()
              .master("local[2]")
              .appName("SparkSessionApp")
              .getOrCreate()
            //2.读取json
            val people = sparkSession.read.json(path)
            //显示json
            people.show()
            //关闭
            sparkSession.stop()
    >    
* 直接在本地测试运行即可.
* 如果遇到NotSuchMethodExption的bug,尝试降低本地Scala和maven中导入的scala依赖的版本为2.11.x;  
    注意,修改本地环境scala版本后,需要在Project Structure -> modules -> dependencies 中修改导入的SDK版本
    
#### Spark-shell/spark-sql脚本的使用
* 需要把hive-site.xml拷贝到spark_home/conf/目录下
* 并在使用命令时使用--jars指定mysql驱动包(该参数可以传多个,用逗号分割)

* Spark-shell: 执行如下命令,启动spark-shell界面
    > spark-shell --master local[2] --jars /zx/hive/lib/mysql-connector-java-5.1.36.jar
    * 在命令行中
        >
            显示所有表
            spark.sql("show tables").show
            查看emp表所有记录
            spark.sql("select * from emp").show
        >
    * 运行时,会有如下error(虽然该error不影响结果)
        > 
            ERROR ObjectStore: Version information found in metastore differs 2.3.0 from expected schema version 1.2.0. Schema verififcation is disabled hive.metastore.schema.verification so setting version.
            在spark/conf/hive-site.xml中添加如下即可
        >

* Spark-sql: 执行如下命令,启动spark-sql界面-和在hive命令行界面一样,直接使用hql操作
    > spark-sql --master local[2] --jars /zx/hive/lib/mysql-connector-java-5.1.36.jar
    * 运行,查询语句,查询
    > select * from emp
    * 可在4040端口查看界面,能看到执行的命令
    * 执行
    >
        创建一张表
        create table t (key string, value string);
        查看查询语句的执行计划(此处无法查看逻辑执行计划,只有物理执行计划)
        explain select a.key*(2+3),b.value from t a join t b on a.key = b.key and a.key >3; 
        查看详细的语句执行计划
        explain extended  select a.key*(2+3),b.value from t a join t b on a.key = b.key and a.key >3; 
        
        分为如下几步:
            1. Parsed Logical Plan:逻辑执行计划
            2. Analyzed Logical Plan:再解析逻辑执行计划,需要和metastore(hive存储在mysql中的元数据)交互
            3. Optimized Logical Plan :优化执行计划
            4. Physical Plan: 物理执行计划,也就是执行操作
    >

#### thriftServer/beeline的使用
* thriftserver用于jdbc的连接的服务,和hive中的hiveserver一样.
* beeline,连接到thriftserver进行操作的命令行服务,和hvie中的beeline相同

* 启动thriftserver,默认端口10000
    >
        ./sbin/start-thriftserver.sh --master local[2] --jars /zx/hive/lib/mysql-connector-java-5.1.36.jar
        可通过--hiveconf hive.server2.thrift.port=xxxx 修改默认端口
        --hiveconf hive.server2.thrift.bind.host=xxx 修改绑定主句主机
        
        启动成功后使用jps -m可查看到一个对应的SparkSubmit  
        可访问4040端口,并可以通过JDBC/ODBC Server页面查看到当前的连接,通过SQL页面查看到执行的sql记录(执行计划等)
        停止执行stop-thriftserver.sh
    >
* 启动beeline
    >
        beeline -u jdbc:hive2://localhost:10000 -n root
        -u是thriftserver的连接路径, -n是当前的linux用户(root)
        
        和操作spark-sql一样即可.
        查看所有表
        show tables;
    >

* thriftserver和spark-shell/spark-sql有什么区别  
    * spark-shell/spark-sql都是一个spark application
    * thriftserver,不管启动多少个客户端(beeline或java api等),都是一个spark application
        并且,多个客户端间可以共享缓存数据等.
        
#### JDBC连接到thriftserver访问SparkSQL
* 依赖(注意,是org.spark-project.hive的hive-jdbc)
>
    <dependency>
      <groupId>org.spark-project.hive</groupId>
      <artifactId>hive-jdbc</artifactId>
      <version>1.2.1.spark2</version>
    </dependency>
>
* 代码如下
>
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
>

#### DataFrame & Dataset & RDD
* RDD -> DataFrame -> Dataset

* RDD: 一个分布式的无序的列表
* Dateset: 一个分布式的数据集合.
* DataFrame: 以列（列名、列的类型、列值）的形式构成的分布式数据集(Dataset)，按照列赋予不同的名称.(不是Spark SQL提出，而是在R、Pandas语言就已有。)
    * 可以直接将其等价理解为一张表.
    * 数据可以从 结构化数据文件/Hive中的表/其他外部数据源/或者RDD转换过来.也有查询/过滤/聚合等操作.
    * Spark1.3之前,它被称为SchemaRDD.
* Dataset可以理解为DataFrame的一个特列.主要区别在于Dataset的每个record存储的是一个强类型值,而不是一个row.

* DataFrame和RDD的区别
![](img/1.png)
    >
        RDD：各个语言运行在自己的环境中 
        	java/scala  ==> jvm
        	python ==> python runtime
        DataFrame: 所有计算被转换为逻辑执行计划,性能相同
        	java/scala/python ==> Logic Plan
        	
        	
       RDD,例如以Person为类型的RDD,Spark框架本身不了解其内部结构,需要用户写一个特定的聚合函数来完成对应功能,
       而DataFrame却提供了详细的信息(schema),可以更好的进行优化.
    >
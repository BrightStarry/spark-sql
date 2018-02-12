#### SparkStreaming 

#### Flume 日志收集
- 该框架也是用Java写的
- 架构图
![](img/spark-streaming/1.png)
    - source: 数据输入源
    - channel: 数据通道
    - sink: 输出
    - agent:source/channel/sink统称为agent
- 安装
    - 前置条件: JDK/足够的内存和硬盘/Read or Write 目录的权限
    - 下载解压,并配置环境变量
    - 拷贝conf/flume-env.sh.template 到 conf/flume-env.sh
        - 在其中配置export JAVA_HOME=xx
    - 命令
        - flume-ng version 查看版本 测试是否安装成功

- 使用
    - 从指定网络端口采集数据输出到控制台
        - 使用Flume的关键就在于写配置文件:配置Source/Channel/Sink,并将其串起来
        - 文档中的例子
        ```
            # example.conf: 一个单节点flume
            # a1: Agent的名字
            # r1: Source的名字
            # k1: Sink的名字
            # c1: Channel的名字
            a1.sources = r1
            a1.sinks = k1
            a1.channels = c1
            
            # Describe/configure the source
            # a1这个agent的sources中的r1这个source的type是netcat(监听某个端口)
            a1.sources.r1.type = netcat
            # 绑定的ip是本机
            a1.sources.r1.bind = hadoop000
            # 端口
            a1.sources.r1.port = 44444
            
           
            # Describe the sink
            # sink的类别为logger,输出info级别的日志到控制台
            a1.sinks.k1.type = logger
            
            # Use a channel which buffers events in memory
            # channel的类型为内存
            a1.channels.c1.type = memory
            
            # Bind the source and sink to the channel
            # 将a1的source r1的channel 绑定为c1, 一个source可以输出到多个channel
            a1.sources.r1.channels = c1
            # 将a1的sink k1的channel 绑定为c1, 一个sink只能绑定到的单个channel
            a1.sinks.k1.channel = c1
        ```
        - 将如上保存为example.conf,运行
        ```
            flume-ng agent --name a1 --conf  $FLUME_HOME/conf --conf-file $FLUME_HOME/conf/example.conf -Dflume.root.logger=INFO,console
            
            -n表示agent名字 *
            -c表示配置文件目录
            -f表示这个我们自己的写的agent配置文件路径
        ```
        - 使用telnet测试(如果没有该命令,可用yum install telnet 安装)
        ```
            连接到本机4444端口
            telnet hadoop000  44444 
            然后直接输入,可发送信息,在之前的ssh控制台上即会输出日志,例如消息为xxx的输出日志
            
            2018-02-11 16:45:13,595 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 66 0D   xxx. }
            其中,Event是Flume传输的基本单元. Event = [header] + byte array   
            ```
    - 监控一个文件，实时采集新增的数据到控制台
        - agent类别： exec source + memory channel + logger sink
        - 配置文件
        ```
            # a1: Agent的名字
            # r1: Source的名字
            # k1: Sink的名字
            # c1: Channel的名字
            a1.sources = r1
            a1.sinks = k1
            a1.channels = c1
            
            # Describe/configure the source
            # source类型为exec 
            a1.sources.r1.type = exec
            # 执行的命令为，滚动监听某个日志文件
            a1.sources.r1.command = tail -F /zx/apache-flume-1.8.0-bin/test.log
            # shell的路径（使用echo $SHELL 输出shell环境变量的路径）
            a1.sources.r1.shell = /bin/sh -c
           
            # Describe the sink
            # sink的类别为logger,输出info级别的日志到控制台
            a1.sinks.k1.type = logger
            
            # Use a channel which buffers events in memory
            # channel的类型为内存
            a1.channels.c1.type = memory
            
            # Bind the source and sink to the channel
            # 将a1的source r1的channel 绑定为c1, 一个source可以输出到多个channel
            a1.sources.r1.channels = c1
            # 将a1的sink k1的channel 绑定为c1, 一个sink只能绑定到的单个channel
            a1.sinks.k1.channel = c1
        ```
        - 将如上内容保存为exec-memory-logger.conf,并创建test.log文件,然后运行
        ```
             flume-ng agent --name a1 --conf  $FLUME_HOME/conf --conf-file $FLUME_HOME/conf/exec-memory-logger.conf -Dflume.root.logger=INFO,console
        ```
        - 然后运行如下shell将字符串输入到test.log文件,即可在控制台看到输出
        ```
            echo hello world >> /zx/apache-flume-1.8.0-bin/test.log 
        ```
    
    - 将A服务器上的日志实时采集到B服务器
        - 两台机器的agent选型
            - A机器（日志源机器） exec source + memory channel + avro sink
            - B机器（目标机器） avro source + memory channel + logger sink
            - avro是一种序列化方式，使用该序列化方式向指定ip:port传输数据
        - 流程： A机器监听本机日志文件，当有更新时，将其通过avro输出； B机器监听对应avro，收到消息后，将其输出到控制台
            
        - A机器配置文件： exec-memory-avro.conf
        ```
            exec-memory-avro.sources = exec-source
            exec-memory-avro.sinks = avro-sink
            exec-memory-avro.channels = memory-channel
            
            exec-memory-avro.sources.exec-source.type = exec
            exec-memory-avro.sources.exec-source.command = tail -F /zx/apache-flume-1.8.0-bin/test.log
            exec-memory-avro.sources.exec-source.shell = /bin/sh -c
           
            exec-memory-avro.sinks.avro-sink.type = avro
            exec-memory-avro.sinks.avro-sink.hostname = hadoop000
            exec-memory-avro.sinks.avro-sink.port = 44444
            
            exec-memory-avro.channels.memory-channel.type = memory
            
            exec-memory-avro.sources.exec-source.channels = memory-channel
            exec-memory-avro.sinks.avro-sink.channel = memory-channel
        ```
        
        - B机器配置文件： avro-memory-logger.conf
        ```
            avro-memory-logger.sources = avro-source
            avro-memory-logger.sinks = logger-sink
            avro-memory-logger.channels = memory-channel
            
            avro-memory-logger.sources.avro-source.type = avro
            avro-memory-logger.sources.avro-source.bind = hadoop000
            avro-memory-logger.sources.avro-source.port = 44444
           
            avro-memory-logger.sinks.logger-sink.type = logger
            
            avro-memory-logger.channels.memory-channel.type = memory
            
            avro-memory-logger.sources.avro-source.channels = memory-channel
            avro-memory-logger.sinks.logger-sink.channel = memory-channel
        ```
        
        - 启动(注意修改 --name和 agent的名字相同，否则会提示： No configuration found for this host: xxx)
            - 先启动B机器，开始监听A机器
            >  flume-ng agent --name  avro-memory-logger --conf  $FLUME_HOME/conf --conf-file $FLUME_HOME/conf/avro-memory-logger.conf -Dflume.root.logger=INFO,console
            - 然后启动A机器，开始收集日志
            >  flume-ng agent --name exec-memory-avro --conf  $FLUME_HOME/conf --conf-file $FLUME_HOME/conf/exec-memory-avro.conf -Dflume.root.logger=INFO,console


#### Kafka 
- 其他详见Kafka项目
- 常用命令
```
    创建topic: zk
    kafka-topics.sh --create --zookeeper hadoop000:2181 --replication-factor 1 --partitions 1 --topic hello_topic
    
    查看所有topic
    kafka-topics.sh --list --zookeeper hadoop000:2181
    
    发送消息: broker
    kafka-console-producer.sh --broker-list hadoop000:9092 --topic hello_topic
    
    消费消息: zk
    kafka-console-consumer.sh --zookeeper hadoop000:2181 --topic hello_topic --from-beginning
    
    
    --from-beginning的使用
    
    查看所有topic的详细信息：kafka-topics.sh --describe --zookeeper hadoop000:2181
    查看指定topic的详细信息：kafka-topics.sh --describe --zookeeper hadoop000:2181 --topic hello_topic
```

- 单节点多broker
```
    server-1.properties
    	log.dirs=/home/hadoop/app/tmp/kafka-logs-1
    	listeners=PLAINTEXT://:9093
    	broker.id=1
    
    server-2.properties
    	log.dirs=/home/hadoop/app/tmp/kafka-logs-2
    	listeners=PLAINTEXT://:9094
    	broker.id=2
    
    server-3.properties
    	log.dirs=/home/hadoop/app/tmp/kafka-logs-3
    	listeners=PLAINTEXT://:9095
    	broker.id=3
    
    kafka-server-start.sh -daemon $KAFKA_HOME/config/server-1.properties &
    kafka-server-start.sh -daemon $KAFKA_HOME/config/server-2.properties &
    kafka-server-start.sh -daemon $KAFKA_HOME/config/server-3.properties &
```

- Kafka API 使用
    - 导入依赖
    ```xml
        <dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
			<version>2.1.0.RELEASE</version>
		</dependency>
    ```
    - 在该scala项目的src/main下新建java目录（原先只有scala目录），并标记为sources root(IDEA)
    - 其余可参考Kafka项目
    - 我将kafka的东西和该项目整合了下。将该项目配置成了spring boot项目。其中遇到的一些bug是jar包冲突了。使用maven的exclusions移除即可
    （IDEA的查找jar依赖关系图十分好用，不仅可以搜索，还可以双击查看是哪个pom的依赖）

#### 整合Flume和Kafka完成数据采集
![](img/9.png
- Flume配置 avro-memory-kafka.conf
```properties
      avro-memory-kafka.sources = avro-source
      avro-memory-kafka.sinks = kafka-sink
      avro-memory-kafka.channels = memory-channel
      
      avro-memory-kafka.sources.avro-source.type = avro
      avro-memory-kafka.sources.avro-source.bind = hadoop000
      avro-memory-kafka.sources.avro-source.port = 44444
     
      avro-memory-kafka.sinks.kafka-sink.type = org.apache.flume.sink.kafka.KafkaSink
      avro-memory-kafka.sinks.kafka-sink.kafka.bootstrap.servers = hadoop000:9092
      avro-memory-kafka.sinks.kafka-sink.kafka.topic = flume-topic
      avro-memory-kafka.sinks.kafka-sink.flumeBatchSize	= 5 
      avro-memory-kafka.sinks.kafka-sink.kafka.producer.acks = 1
      
      avro-memory-kafka.channels.memory-channel.type = memory
      
      avro-memory-kafka.sources.avro-source.channels = memory-channel
      avro-memory-kafka.sinks.kafka-sink.channel = memory-channel

```

- 启动
    - 启动该项目的kafka consumer api
    - 启动B机器，开始监听A机器，将A机器收集的日志输出到kafka
    >  flume-ng agent --name  avro-memory-kafka --conf  $FLUME_HOME/conf --conf-file $FLUME_HOME/conf/avro-memory-kafka.conf -Dflume.root.logger=INFO,console
    - 启动A机器，开始收集日志
    >  flume-ng agent --name exec-memory-avro --conf  $FLUME_HOME/conf --conf-file $FLUME_HOME/conf/exec-memory-avro.conf -Dflume.root.logger=INFO,console
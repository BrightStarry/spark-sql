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


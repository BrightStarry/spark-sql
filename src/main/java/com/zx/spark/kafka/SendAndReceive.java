package com.zx.spark.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2018/1/5 0005 12:50
 * 测试 发送和接收 消息
 */
@Slf4j
public class SendAndReceive {
	//线程数量
	private static final int threadSize = 1;
	//消息监听器容器 的beanName
	private static final String beanName = "testSendAndReceive";
	//主题
	private static final String topic1 = "topic1";
	private static final String topic2 = "topic2";

	public static void main(String[] args) throws InterruptedException {
		sendAndReceive();
	}

	public static void sendAndReceive() throws InterruptedException {
		log.info("开始");

		//消费者相关---------------------------------------------------------------------------
		//消息监听器容器属性 配置两个主题
		ContainerProperties containerProperties = new ContainerProperties(topic1, topic2);
		//门闩
		final CountDownLatch latch = new CountDownLatch(threadSize);
		//创建消费者相关配置的map
		Map<String, Object> consumePropMap = createConsumePropMap();
		//创建出消费者工厂,注入属性 泛型分别为消息的key和value
		DefaultKafkaConsumerFactory<Integer, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumePropMap);
		//创建出 消息监听器容器,使用 容器属性 和 默认消费者工厂
		KafkaMessageListenerContainer<Integer, String> messageListenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
		//设置消息监听器, 其实是调用了ContainerProperties的设置方法,
		messageListenerContainer.setupMessageListener((MessageListener<Integer, String>) message -> {
			log.info("接收到消息:{}",message);
			latch.countDown();
		});
		//设置 消息监听器容器 的beanName
		messageListenerContainer.setBeanName(beanName);
		//启动容器
		messageListenerContainer.start();
		//等待一下,让容器异步启动
		Thread.sleep(10000);

		//生产者相关-------------------------------------------------------------------------------------
		//创建 生产者 相关配置的 map
		Map<String, Object> producePropMap = createProducePropMap();
		//创建生产者工厂,注入属性
		DefaultKafkaProducerFactory<Integer, String> producerFactory = new DefaultKafkaProducerFactory<>(producePropMap);
		//创建出发送模版 使用生产者工厂
		KafkaTemplate<Integer, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
		//设置默认发送的主题
		kafkaTemplate.setDefaultTopic(topic1);
		//发送若干数据
		for (int i = 0; i < 1; i++) {
			kafkaTemplate.sendDefault(i, "msg..........." + i);
		}
		kafkaTemplate.flush();

		//发送完成后,门闩等待...消费者每消费一条消息,就会释放一个门闩.所以,消费者消费完10条消息后,才会释放门闩, 最多等待30秒
		latch.await(30, TimeUnit.SECONDS);

		//停止消费者监听容器
		messageListenerContainer.stop();

		log.info("结束");
	}

	/**
	 * 创建消费者相关配置的map
	 */
	private static Map<String, Object> createConsumePropMap() {
		Map<String, Object> props = new HashMap<>();
		//连接地址
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "106.14.7.29:9092");
		//消费者组,标识这个消费者所属的消费者组,一个唯一的字符串
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-1");
		//是否自动提交消费者的offset(偏移量),也就是该消费者目前在分区日志中的位置
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		//自动提交消费者偏移量的间隔,毫秒. 如果ENABLE_AUTO_COMMIT_CONFIG属性开启,它就会生效
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		//服务端会对消费者心跳检测.如果该消费者超过该时间未响应.则剔除
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		//key反序列化
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
		//value反序列化
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return props;
	}


	/**
	 * 创建 生产者 相关配置的 map
	 */
	private static Map<String,Object> createProducePropMap() {
		Map<String, Object> props = new HashMap<>();
		//kafka连接地址
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "106.14.7.29:9092");
		//重试次数
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		//当有多个记录被发送到同一个分区,生产者尝试将多个记录合并到一个请求中,该配置就是默认批量提交的每一批的最大字节大小.. 过小可能还会降低吞吐量,过大会占用内存
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		//对于上面的批量发送,给配置可以设置主动等待x毫秒,以便让每次请求,发送的消息尽可能的多.以提高性能. 默认为0,也就是不等待
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		//生产者可以缓存的.等待被发送的记录的总字节数
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		//消息的key的序列花器, 使用了kafka自带的Integer序列化器
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		//消息的value的序列化器,使用了kafka自带的String序列化器
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}
}

package com.zx.spark.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2018/1/5 0005 16:44
 * spring boot 整合 kafka 测试
 */
@Slf4j
@Component
public class Kafka {
	//发送模版
	@Autowired
	private KafkaTemplate<Integer,String> kafkaTemplate;

	//发送
	public void send() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			kafkaTemplate.send("topic1", 0,"这是一条消息." + i);

		}
	}

	//监听该主题的消息
	@KafkaListener(topicPartitions = {@TopicPartition(topic = "flume-topic",partitions = "0")})
	public void listen3(ConsumerRecord<Integer,String> consumerRecord) throws InterruptedException {
		log.info("收到消息:{}",consumerRecord);
		Thread.sleep(500);
	}

	//监听该主题的消息
	@KafkaListener(topicPartitions = {@TopicPartition(topic = "topic1",partitions = "0")})
	public void listen(ConsumerRecord<Integer,String> consumerRecord) throws InterruptedException {
		log.info("1分区 - 收到消息:{}",consumerRecord);
		Thread.sleep(500);
	}

	//监听该主题的消息
	@KafkaListener(topicPartitions = {@TopicPartition(topic = "topic1",partitions = "1")})
	public void listen2(ConsumerRecord<Integer,String> consumerRecord) throws InterruptedException {
		log.info("1分区 - 收到消息:{}",consumerRecord);
		Thread.sleep(500);
	}
}

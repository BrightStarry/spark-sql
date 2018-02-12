package com.zx.spark;

import com.zx.spark.kafka.Kafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SparkApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(SparkApplication.class, args);
	}


	@Autowired
	private Kafka kafka;

	@Override
	public void run(String... strings) throws Exception {
//		kafka.send();
	}
}

package com.logistics.LogisticsFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = "com.logistics.LogisticsFuture")
@EntityScan("com.logistics.LogisticsFuture")
@PropertySource("classpath:application.yml")
public class LogisticsFutureApplication {

	public static void main(String[] args) {
	SpringApplication.run(LogisticsFutureApplication.class, args);

	}

}

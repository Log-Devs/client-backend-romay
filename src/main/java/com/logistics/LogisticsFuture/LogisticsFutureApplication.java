package com.logistics.LogisticsFuture;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;


@OpenAPIDefinition(
		info = @Info(title = "LogisticsFuture API", version = "1.0", description = "API documentation for authentication & logistics operations.")
)
@SpringBootApplication(scanBasePackages = "com.logistics.LogisticsFuture")
@EntityScan("com.logistics.LogisticsFuture")
@PropertySource("classpath:application.yml")
public class LogisticsFutureApplication {

	public static void main(String[] args) {
	SpringApplication.run(LogisticsFutureApplication.class, args);

	}

}

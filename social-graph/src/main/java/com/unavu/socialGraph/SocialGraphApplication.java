package com.unavu.socialGraph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.unavu.socialGraph", "com.unavu.common"})
@EnableFeignClients
public class SocialGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialGraphApplication.class, args);
	}

}

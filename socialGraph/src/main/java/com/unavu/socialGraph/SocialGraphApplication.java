package com.unavu.socialGraph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class SocialGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialGraphApplication.class, args);
	}

}

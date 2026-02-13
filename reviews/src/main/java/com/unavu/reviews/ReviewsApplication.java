package com.unavu.reviews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {"com.unavu.reviews", "com.unavu.common"})
public class ReviewsApplication {

    @Autowired
    private AuditorAware<String> auditorAware;
	public static void main(String[] args) {
		SpringApplication.run(ReviewsApplication.class, args);
	}

}

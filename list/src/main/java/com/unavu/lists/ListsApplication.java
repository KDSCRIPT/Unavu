package com.unavu.lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {"com.unavu.lists", "com.unavu.common"})
public class ListsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ListsApplication.class, args);
	}

}

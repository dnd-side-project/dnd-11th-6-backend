package com.dnd.snappy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SnappyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnappyApplication.class, args);
	}

}

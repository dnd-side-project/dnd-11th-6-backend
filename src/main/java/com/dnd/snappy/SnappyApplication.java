package com.dnd.snappy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SnappyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnappyApplication.class, args);
	}

}

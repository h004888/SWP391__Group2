package com.OLearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OLearningApplication {
	public static void main(String[] args) {
		SpringApplication.run(OLearningApplication.class, args);
	}
}

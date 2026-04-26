package com.smj.workhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class WorkhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkhubApplication.class, args);
	}

}

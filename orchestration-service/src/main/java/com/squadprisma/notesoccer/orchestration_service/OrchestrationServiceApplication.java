package com.squadprisma.notesoccer.orchestration_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OrchestrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrchestrationServiceApplication.class, args);
	}

}

package com.cognizant.researcher_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ResearcherServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResearcherServiceApplication.class, args);

	}

}

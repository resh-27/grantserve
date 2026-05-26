package com.cognizant.audit_compliance_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AuditComplianceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuditComplianceServiceApplication.class, args);
	}

}

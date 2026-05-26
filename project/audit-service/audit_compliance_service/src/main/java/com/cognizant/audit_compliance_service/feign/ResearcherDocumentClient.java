package com.cognizant.audit_compliance_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// "researcher-service" must match the name in your Eureka Registry
@FeignClient(name = "researcher-service")
public interface ResearcherDocumentClient {

    // This signature MUST match the endpoint you just created in your controller
    @PutMapping("/api/documents/{id}/status")
    String updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status
    );
}

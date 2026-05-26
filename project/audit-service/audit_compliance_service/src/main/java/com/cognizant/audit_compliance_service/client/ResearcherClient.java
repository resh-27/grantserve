package com.cognizant.audit_compliance_service.client;

import com.cognizant.audit_compliance_service.dto.AuditLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Add contextId to make this bean unique
@FeignClient(name = "AUDIT-LOG-SERVICE", contextId = "researcherAuditClient")
public interface ResearcherClient {

    @PostMapping("/api/v1/audit-log/add")
    void addLog(@RequestBody AuditLogDTO auditLogDTO);
}
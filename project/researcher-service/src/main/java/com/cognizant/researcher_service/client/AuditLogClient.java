package com.cognizant.researcher_service.client;

import com.cognizant.researcher_service.dto.AuditLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUDIT-LOG-SERVICE")
public interface AuditLogClient {

    @PostMapping("/api/v1/audit-log/add")
    void addLog(@RequestBody AuditLogDTO auditLogDTO);
}

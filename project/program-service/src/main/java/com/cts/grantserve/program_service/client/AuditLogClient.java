package com.cts.grantserve.program_service.client;

import com.cts.grantserve.program_service.dto.AuditLogDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "AUDIT-LOG-SERVICE")
public interface AuditLogClient {

    @PostMapping("/api/v1/audit-log/add")
    void addLog(AuditLogDTO logDTO);

}

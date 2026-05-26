package com.cts.grantserve.audit_log_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuditLogDTO {

    @NotBlank(message = "Action cannot be blank")
    @Size(max = 100)
    private String action;

    @NotBlank(message = "Resource cannot be blank")
    @Size(max = 100)
    private String resource;

}
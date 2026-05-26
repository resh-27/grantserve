package com.cognizant.audit_compliance_service.dto;

import com.cognizant.audit_compliance_service.enums.AuditScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuditDto (

    Long auditID,

    @NotNull(message = "Officer ID is mandatory")
    Long officerID,

    @NotNull(message = "Audit scope is mandatory")
    AuditScope scope,

    @NotBlank(message = "Findings cannot be empty")
    @Size(max = 2000, message = "Findings must not exceed 2000 characters")
    String findings

    ){}

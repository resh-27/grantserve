package com.cognizant.audit_compliance_service.dto;

import com.cognizant.audit_compliance_service.enums.ComplianceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComplianceRecordDto (
    Long complianceID,

    @NotNull(message = "Entity ID is mandatory and cannot be null")
    Long entityID,

    @NotNull(message = "Compliance type is required")
    ComplianceType type,

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    String notes
    ){}

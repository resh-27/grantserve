package com.cognizant.researcher_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private String action;
    private String resource;
    // Add other fields if your Audit Service requires them (e.g., userId, timestamp)
}

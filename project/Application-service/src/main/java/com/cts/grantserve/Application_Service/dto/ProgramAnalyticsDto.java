package com.cts.grantserve.Application_Service.dto;

import java.util.Map;

public record ProgramAnalyticsDto(
        Long totalApplications,
        Long approvedApplications,
        Double acceptanceRate,
        Map<String, Object> monthlyStats
) {}
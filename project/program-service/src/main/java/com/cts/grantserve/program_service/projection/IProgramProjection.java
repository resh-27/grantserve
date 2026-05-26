package com.cts.grantserve.program_service.projection;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public interface IProgramProjection {
    Long getProgramID();
    String getTitle();
    String getDescription();
    LocalDate getStartDate();
    LocalDate getEndDate();
    Double getBudget();
    Long getBudgetId();

    // Dynamic logic: If status is ACTIVE and startDate is in the future, return FORECASTED
    @Value("#{target.status.name() == 'ACTIVE' && target.startDate.isAfter(T(java.time.LocalDate).now()) ? 'FORECASTED' : target.status}")
    String getStatus();
}
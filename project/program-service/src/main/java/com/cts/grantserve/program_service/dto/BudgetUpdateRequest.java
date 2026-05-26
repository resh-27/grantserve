package com.cts.grantserve.program_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BudgetUpdateRequest(
    @NotNull(message = "Budget amount is required")
    @Min(value = 1, message = "Budget must be at least 1")
    Double budget
) {}
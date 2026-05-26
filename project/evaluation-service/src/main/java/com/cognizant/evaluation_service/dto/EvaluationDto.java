package com.cognizant.evaluation_service.dto;



import com.cognizant.evaluation_service.enums.Result;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EvaluationDto(
        @NotNull(message = "Application ID is required")
        Long applicationID,

        @NotNull(message = "Result (APPROVED/REJECTED) is required")
        Result result,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Evaluation date cannot be in the future")
        LocalDate date,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes
) {}
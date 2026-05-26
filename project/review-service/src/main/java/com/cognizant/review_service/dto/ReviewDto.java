package com.cognizant.review_service.dto;

import com.cognizant.review_service.enums.ReviewStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ReviewDto(
        @NotNull(message = "Proposal ID is required")
        Long proposalId,

        @NotNull(message = "Reviewer ID is required")
        Long reviewerId,

        @NotNull(message = "Score is required") // Added NotNull for score
        @Min(value = 0, message = "Score must be at least 1")
        @Max(value = 10, message = "Score cannot be more than 10")
        Integer score,

        @NotBlank(message = "Comments cannot be empty")
        @Size(min = 10, max = 1000, message = "Comments should be between 10 and 1000 characters")
        String comments,

        @NotNull(message = "Review date is required")
        @PastOrPresent(message = "Review date cannot be in the future") // Good practice for reviews
        LocalDate date,


        @NotNull(message = "Status is required (PENDING, UNDER_REVIEW, or REVIEWED)")
        ReviewStatus status
) {}
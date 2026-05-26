package com.cognizant.review_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProposalDto(
        @NotNull(message = "Application ID is required")
        Long applicationID,

        @NotBlank(message = "File URI cannot be empty")
        @Size(max = 255, message = "File URI is too long")
        String fileURI
) {}
package com.cognizant.evaluation_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GrantApplicationDto(
        @NotNull(message = "Application ID is required")
        Long applicationID

//        @NotNull(message = "Program ID is required")
//        Long programID,
//
//        @NotBlank(message = "Grant title is required")
//        String title
) {}

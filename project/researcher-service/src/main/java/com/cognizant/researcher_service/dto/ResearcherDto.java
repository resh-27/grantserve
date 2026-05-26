package com.cognizant.researcher_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ResearcherDto(


        @NotBlank(message = "Name is required")
        String name,

        //@NotNull(message = "User ID is required")
        //Long userid,

        @NotBlank(message = "Contact info is required")
        String contactInfo,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,

        @NotBlank(message = "Gender is required")
        @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
        String gender,

        @NotBlank(message = "Institution is required")
        String institution,

        @NotBlank(message = "Department is required")
        String department
) {}


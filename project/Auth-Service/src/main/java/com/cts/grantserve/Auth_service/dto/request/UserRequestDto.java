package com.cts.grantserve.Auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record UserRequestDto(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s'-]+$",
                message = "Name can only contain letters, spaces, hyphens, or apostrophes")
        String name,

        @NotBlank(message = "Email is required")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10}$",
                message = "Phone number must be exactly 10 digits")
        String phone,

        @NotBlank(message = "Role is required")
        @Pattern(
                regexp = "^(RESEARCHER|REVIEWER|MANAGER|ADMIN|COMPLIANCE|AUDITOR)$",
                message = "Invalid role. Must be Researcher, Reviewer, Manager, Admin, Compliance, or Auditor"
        )
        String role,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
                message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character")
        String password

) {}
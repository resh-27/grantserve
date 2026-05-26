package com.cognizant.review_service.dto;


public record UserResponseDto(
        Long userid,
        String name,
        String role,
        String token
) {}
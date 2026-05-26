package com.cts.grantserve.Auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@NoArgsConstructor  // Necessary for JSON tools
@AllArgsConstructor // This fixes the "Expected no arguments" error
public class UserResponseDto extends BaseUserResponseDto {
    private String token;
    private String role;
    private Long userid;
    private String name;

    // Custom constructor for your Feign/Auth call
    public UserResponseDto(Long userid, String name, String role) {
        this.userid = userid;
        this.name = name;
        this.role = role;
    }
}
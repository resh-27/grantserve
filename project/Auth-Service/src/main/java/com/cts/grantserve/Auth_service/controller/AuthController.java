package com.cts.grantserve.Auth_service.controller;

import com.cts.grantserve.Auth_service.dto.request.UserRequestDto;
import com.cts.grantserve.Auth_service.dto.response.BaseUserResponseDto;
import com.cts.grantserve.Auth_service.dto.response.UserResponseDto;
import com.cts.grantserve.Auth_service.entity.User;
import com.cts.grantserve.Auth_service.service.IUserService;
import com.cts.grantserve.Auth_service.service.JWTService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    IUserService userService;

    @Autowired
    JWTService jwtService;

    @PostMapping("/register")
    public ResponseEntity<BaseUserResponseDto> registerUser(@Valid @RequestBody UserRequestDto user){
        return userService.registerUser(user);
    }
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> UserLoginValidation(@RequestBody User user){
        return userService.UserLoginValidation(user);
    }
    // Inside UserController.java (User Service)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        // Map your User entity to a DTO that contains the ID, Name, and Role
        return ResponseEntity.ok(new UserResponseDto(user.getUserID(), user.getName(), user.getRole()));
    }
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable("role") String role) {
        log.info("Fetching all users with role: {}", role);
        List<UserResponseDto> users = userService.findUsersByRole(role);
        return ResponseEntity.ok(users);
    }

}

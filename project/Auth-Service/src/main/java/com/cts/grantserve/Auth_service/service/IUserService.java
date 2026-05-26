package com.cts.grantserve.Auth_service.service;


import com.cts.grantserve.Auth_service.dto.request.UserRequestDto;
import com.cts.grantserve.Auth_service.dto.response.BaseUserResponseDto;
import com.cts.grantserve.Auth_service.dto.response.UserResponseDto;
import com.cts.grantserve.Auth_service.entity.User;
import com.cts.grantserve.Auth_service.exception.UserException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {

    public ResponseEntity<BaseUserResponseDto> registerUser(UserRequestDto user) throws UserException;

    ResponseEntity<UserResponseDto> UserLoginValidation(User user);
    User findById(Long id);
    List<UserResponseDto> findUsersByRole(String role);


}

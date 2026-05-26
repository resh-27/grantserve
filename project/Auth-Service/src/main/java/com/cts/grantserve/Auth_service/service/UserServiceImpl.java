package com.cts.grantserve.Auth_service.service;

import com.cts.grantserve.Auth_service.dto.request.UserRequestDto;
import com.cts.grantserve.Auth_service.dto.response.BaseUserResponseDto;
import com.cts.grantserve.Auth_service.dto.response.RegisterResponse;
import com.cts.grantserve.Auth_service.dto.response.UserResponseDto;
import com.cts.grantserve.Auth_service.entity.User;
import com.cts.grantserve.Auth_service.exception.UserException;
//import com.cts.grantserve.Auth_service.feign.IResearcherFeign;
import com.cts.grantserve.Auth_service.feign.IResearcherFeign;
import com.cts.grantserve.Auth_service.repository.IUserRepository;
import com.cts.grantserve.Auth_service.util.ClassUtilSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserRepository userDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IResearcherFeign iResearcherFeign;

    public ResponseEntity<BaseUserResponseDto> registerUser(UserRequestDto userRequestDto) throws UserException {
        if (userDAO.existsByEmail(userRequestDto.email())) {
            throw new UserException("Email already registered!", HttpStatus.CONFLICT);
        }
        User user = ClassUtilSeparator.userRegisterUtil(userRequestDto);
        user.setStatus("Active");
        user.setPassword(passwordEncoder.encode(userRequestDto.password()));
        userDAO.save(user);
        if(user.getRole().equals("RESEARCHER")){
            RegisterResponse response = new RegisterResponse(user.getName(),user.getUserID());
            iResearcherFeign.registerResearcher(response);
        }
        BaseUserResponseDto response = new BaseUserResponseDto();
        response.setMessage("Registered Successfully");
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTService jwtService;

    @Override
    public ResponseEntity<UserResponseDto> UserLoginValidation(User user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));


        if(authentication.isAuthenticated()) {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String combined = authentication.getName();
            String[] parts = combined.split(":");
            String token = jwtService.generateToken(parts[0], role, Long.valueOf(parts[1]));
            UserResponseDto response = new UserResponseDto();
            response.setToken(token);
            response.setRole(role);
            response.setStatusCode(200);
            response.setMessage("Login Successful");
            response.setUserid(Long.valueOf(parts[1]));
            return ResponseEntity.ok(response);
        } else {
            UserResponseDto response = new UserResponseDto();
            response.setStatusCode(401);
            response.setMessage("Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Override
    public User findById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }


    @Override
    public List<UserResponseDto> findUsersByRole(String role) {
        return userDAO.findByRole(role).stream()
                .map(user -> new UserResponseDto(user.getUserID(), user.getName(), user.getRole()))
                .collect(Collectors.toList());
    }

}




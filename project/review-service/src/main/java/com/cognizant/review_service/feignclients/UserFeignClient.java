package com.cognizant.review_service.feignclients;



import com.cognizant.review_service.dto.UserResponseDto; // Create a matching DTO in Review Service
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "api-gateway", url = "http://localhost:8081/auth-service/auth")
public interface UserFeignClient {
    @GetMapping("/{id}")
    UserResponseDto getUserById(@PathVariable("id") Long id);
}
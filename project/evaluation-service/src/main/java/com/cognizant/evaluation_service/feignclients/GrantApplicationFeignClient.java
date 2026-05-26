package com.cognizant.evaluation_service.feignclients;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="APPLICATION-SERVICE")
public interface GrantApplicationFeignClient {
    @PutMapping("/GrantApplication/updateStatusById")
    public boolean updateStatusById(@RequestParam String status, @RequestParam Long Id);

    @GetMapping("/GrantApplication/checkGrantApplicationExists/{id}")
    public boolean grantApplicationExist(@PathVariable Long id);

}

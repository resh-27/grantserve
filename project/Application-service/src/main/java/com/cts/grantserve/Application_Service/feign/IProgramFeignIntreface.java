package com.cts.grantserve.Application_Service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name ="PROGRAM-SERVICE",configuration = com.cts.grantserve.security.shared.config.FeignConfig.class)
public interface IProgramFeignIntreface {
    @GetMapping("/api/v1/programs/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id);
}

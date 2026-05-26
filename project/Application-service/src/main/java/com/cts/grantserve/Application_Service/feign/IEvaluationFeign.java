package com.cts.grantserve.Application_Service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

@FeignClient(name = "EVALUATION-SERVICE",configuration = com.cts.grantserve.security.shared.config.FeignConfig.class)
public interface IEvaluationFeign {
    @GetMapping("/evaluation/approved-date/{applicationID}")
    ResponseEntity<LocalDate> getApprovedDate(@PathVariable Long applicationID);
}

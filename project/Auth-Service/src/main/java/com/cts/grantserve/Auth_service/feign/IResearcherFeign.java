package com.cts.grantserve.Auth_service.feign;

import com.cts.grantserve.Auth_service.dto.response.RegisterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "RESEARCHER-SERVICE")
public interface IResearcherFeign {

    @PostMapping("/api/researcher/register")
    String registerResearcher(@RequestBody RegisterResponse researcherDto);

}

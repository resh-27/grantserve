package com.cognizant.review_service.feignclients;

import com.cognizant.review_service.dto.ProposalDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "proposal-service",url="http://localhost:8084",path="/proposal")
public interface ProposalFeignClient {

    @PutMapping("/updateStatusById")
    public boolean updateStatusById(@RequestParam String status,@RequestParam Long Id);
    @GetMapping("/checkProposalExists/{id}")
    public boolean proposalExist(@PathVariable Long id);
    @GetMapping("/application/{appId}")
    public ResponseEntity<List<Long>> getIds(@PathVariable Long appId);

}

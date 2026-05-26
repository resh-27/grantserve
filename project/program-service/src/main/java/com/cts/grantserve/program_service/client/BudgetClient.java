package com.cts.grantserve.program_service.client;

import com.cts.grantserve.program_service.dto.BudgetDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cts.grantserve.security.shared.config.FeignConfig;

@FeignClient(name = "BUDGET-SERVICE", configuration = FeignConfig.class)
public interface BudgetClient {

    @GetMapping("/api/v1/budgets/{id}")
    BudgetDto getBudgetById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/budgets/program/{programId}")
    ResponseEntity<BudgetDto> getBudgetByProgramId(@PathVariable("programId") Long programId);

    @PostMapping("/api/v1/budgets/create")
    Long createBudget(@RequestBody BudgetDto budgetDto);

    @PatchMapping("/api/v1/budgets/close/program/{id}")
    String closeBudgetByProgramId(@PathVariable("id") Long id);
}

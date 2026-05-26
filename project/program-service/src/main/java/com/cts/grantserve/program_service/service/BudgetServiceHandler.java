package com.cts.grantserve.program_service.service;

import com.cts.grantserve.program_service.client.BudgetClient;
import com.cts.grantserve.program_service.dto.BudgetDto;
import com.cts.grantserve.program_service.entity.Program;
import com.cts.grantserve.program_service.enums.BudgetStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetServiceHandler {

    private final BudgetClient budgetClient;

    @Retry(name = "budgetService")
    @CircuitBreaker(name = "budgetService")
    public Long initializeBudget(Program program) {
        BudgetDto budgetDto = new BudgetDto(
                null,
                program.getBudget(),
                0.0,
                program.getBudget(),
                BudgetStatus.ACTIVE,
                program.getProgramID()
        );
        return budgetClient.createBudget(budgetDto);
    }

    @Retry(name = "budgetService")
    @CircuitBreaker(name = "budgetService")
    public Long initializeBudget(Long programId, Double budgetAmount) {
        BudgetDto budgetDto = new BudgetDto(
                null,
                budgetAmount,
                0.0,
                budgetAmount,
                BudgetStatus.ACTIVE,
                programId
        );
        return budgetClient.createBudget(budgetDto);
    }

    @Retry(name = "budgetService")
    @CircuitBreaker(name = "budgetService")
    public void getBudgetByProgramId(Long id) {
        budgetClient.getBudgetByProgramId(id);
    }

    @Retry(name = "budgetService")
    @CircuitBreaker(name = "budgetService")
    public String closeBudget(Long id) {
        return budgetClient.closeBudgetByProgramId(id);
    }
}
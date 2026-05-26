package com.cts.grantserve.budget_service.service;

import com.cts.grantserve.budget_service.dto.BudgetDto;
import com.cts.grantserve.budget_service.entity.Budget;
import com.cts.grantserve.budget_service.projection.IBudgetProjection;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IBudgetService {

    @Transactional
    BudgetDto createBudget(BudgetDto budgetDto);

    ResponseEntity<IBudgetProjection> getBudget(Long id);

    ResponseEntity<IBudgetProjection> getBudgetByProgram(Long programId);

    List<IBudgetProjection> getAllBudgets();

    List<IBudgetProjection> getAllBudgetsByProgramIds(List<Long> programIds);

    @Transactional
    String allocateAmountToResearcherByBudgetId(Long id, double allocatedAmount);

    @Transactional
    String updateBudgetStatusToClosed(Long programId);

}
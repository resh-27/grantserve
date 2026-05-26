package com.cts.grantserve.budget_service.service;

import com.cts.grantserve.budget_service.dto.BudgetDto;
import com.cts.grantserve.budget_service.entity.Budget;
import com.cts.grantserve.budget_service.enums.BudgetStatus;
import com.cts.grantserve.budget_service.exception.*;
import com.cts.grantserve.budget_service.projection.IBudgetProjection;
import com.cts.grantserve.budget_service.repository.BudgetRepository;
import com.cts.grantserve.budget_service.util.ClassUtilSeparator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements IBudgetService {

    private final BudgetRepository budgetRepository;

    @Transactional
    @Override
    public BudgetDto createBudget(BudgetDto budgetDto) {
        Budget budget = ClassUtilSeparator.budgetUtil(budgetDto);
        budget.setProgramId(budgetDto.programId());
        return ClassUtilSeparator.convertToDto(budgetRepository.save(budget));
    }

    @Transactional
    @Override
    public String allocateAmountToResearcherByBudgetId(Long id, double allocatedAmount) {
        Budget existingBudget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found with id: " + id));

        if (existingBudget.getStatus() == BudgetStatus.CLOSED) {
            throw new BudgetClosedException("Budget is closed.");
        }
        if (existingBudget.getRemainingAmount() < allocatedAmount) {
            throw new InsufficientFundsException("Insufficient funds.");
        }

        existingBudget.setSpentAmount(existingBudget.getSpentAmount() + allocatedAmount);
        existingBudget.setRemainingAmount(existingBudget.getRemainingAmount() - allocatedAmount);

        budgetRepository.save(existingBudget);

        log.info("Allocation successful for Budget ID: {}. New remaining amount: {}", id, existingBudget.getRemainingAmount());
        return "Amount allocated to researcher successfully";
    }

    @Override
    public ResponseEntity<IBudgetProjection> getBudget(Long id) {
        return budgetRepository.findProjectedByBudgetID(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found with id: " + id));
    }

    @Override
    public ResponseEntity<IBudgetProjection> getBudgetByProgram(Long programId) {
        return budgetRepository.findProjectedByProgramId(programId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BudgetNotFoundException("No budget for Program ID: " + programId));
    }

    @Override
    public List<IBudgetProjection> getAllBudgets() {
        return budgetRepository.findAllProjectedBy();
    }

    @Override
    public List<IBudgetProjection> getAllBudgetsByProgramIds(List<Long> programIds) {
        if (programIds == null || programIds.isEmpty()) {
            return Collections.emptyList();
        }

        return budgetRepository.findAllProjectedByProgramIdIn(programIds);
    }

    @Transactional
    @Override
    public String updateBudgetStatusToClosed(Long programId) {
        int rowsAffected = budgetRepository.updateBudgetStatusToClosed(programId);
        if (rowsAffected == 0) {
            throw new BudgetNotModifiableException("Cannot close budget. Either the program ID is Invalid or the budget is already in CLOSED status.");
        }

        log.info("Budget for Program ID: {} successfully updated to CLOSED status.", programId);
        return "Budget status updated to CLOSED successfully.";
    }
}
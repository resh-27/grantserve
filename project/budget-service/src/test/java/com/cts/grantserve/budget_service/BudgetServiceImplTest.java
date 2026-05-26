package com.cts.grantserve.budget_service;

import com.cts.grantserve.budget_service.dto.BudgetDto;
import com.cts.grantserve.budget_service.entity.Budget;
import com.cts.grantserve.budget_service.enums.BudgetStatus;
import com.cts.grantserve.budget_service.exception.*;
import com.cts.grantserve.budget_service.projection.IBudgetProjection;
import com.cts.grantserve.budget_service.repository.BudgetRepository;
import com.cts.grantserve.budget_service.service.BudgetServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @Test
    @DisplayName("Create Budget - Success")
    void createBudget_success() {
        BudgetDto dto = new BudgetDto(
                null, 1000.0, 0.0, 1000.0,
                BudgetStatus.ACTIVE, 1L
        );

        Budget savedBudget = new Budget();
        savedBudget.setAllocatedAmount(1000.0);
        savedBudget.setSpentAmount(0.0);
        savedBudget.setRemainingAmount(1000.0);
        savedBudget.setStatus(BudgetStatus.ACTIVE);
        savedBudget.setProgramId(1L);

        when(budgetRepository.save(any(Budget.class))).thenReturn(savedBudget);

        BudgetDto result = budgetService.createBudget(dto);

        assertNotNull(result);
        assertEquals(1000.0, result.allocatedAmount());
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    @DisplayName("Allocate Amount - Success")
    void allocateAmount_success() {
        Budget budget = new Budget();
        budget.setBudgetID(1L);
        budget.setStatus(BudgetStatus.ACTIVE);
        budget.setSpentAmount(100.0);
        budget.setRemainingAmount(900.0);

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        String result = budgetService
                .allocateAmountToResearcherByBudgetId(1L, 200.0);

        assertEquals("Amount allocated to researcher successfully", result);
        assertEquals(300.0, budget.getSpentAmount());
        assertEquals(700.0, budget.getRemainingAmount());
        verify(budgetRepository).save(budget);
    }

    @Test
    @DisplayName("Allocate Amount - Budget Not Found")
    void allocateAmount_budgetNotFound() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                BudgetNotFoundException.class,
                () -> budgetService
                        .allocateAmountToResearcherByBudgetId(1L, 100.0)
        );
    }

    @Test
    @DisplayName("Allocate Amount - Budget Closed Exception")
    void allocateAmount_budgetClosed() {
        Budget budget = new Budget();
        budget.setStatus(BudgetStatus.CLOSED);

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        assertThrows(
                BudgetClosedException.class,
                () -> budgetService
                        .allocateAmountToResearcherByBudgetId(1L, 100.0)
        );
    }

    @Test
    @DisplayName("Allocate Amount - Insufficient Funds Exception")
    void allocateAmount_insufficientFunds() {
        Budget budget = new Budget();
        budget.setStatus(BudgetStatus.ACTIVE);
        budget.setRemainingAmount(50.0);

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        assertThrows(
                InsufficientFundsException.class,
                () -> budgetService
                        .allocateAmountToResearcherByBudgetId(1L, 100.0)
        );
    }

    @Test
    @DisplayName("Get Budget By Program ID - Found")
    void getBudgetByProgram_success() {
        IBudgetProjection projection = mock(IBudgetProjection.class);

        when(budgetRepository.findProjectedByProgramId(1L))
                .thenReturn(Optional.of(projection));

        ResponseEntity<IBudgetProjection> response =
                budgetService.getBudgetByProgram(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get Budget By Program ID - Not Found")
    void getBudgetByProgram_notFound() {
        when(budgetRepository.findProjectedByProgramId(1L))
                .thenReturn(Optional.empty());

        ResponseEntity<IBudgetProjection> response =
                budgetService.getBudgetByProgram(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Update Budget Status to Closed - Success")
    void updateBudgetStatusToClosed_success() {
        when(budgetRepository.updateBudgetStatusToClosed(1L))
                .thenReturn(1);

        String result = budgetService.updateBudgetStatusToClosed(1L);

        assertEquals("Budget status updated to CLOSED successfully.", result);
    }

    @Test
    @DisplayName("Update Budget Status to Closed - Failure")
    void updateBudgetStatusToClosed_failure() {
        when(budgetRepository.updateBudgetStatusToClosed(1L))
                .thenReturn(0);

        assertThrows(
                BudgetNotModifiableException.class,
                () -> budgetService.updateBudgetStatusToClosed(1L)
        );
    }
}
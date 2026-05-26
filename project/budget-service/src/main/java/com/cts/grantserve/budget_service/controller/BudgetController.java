package com.cts.grantserve.budget_service.controller;

import com.cts.grantserve.budget_service.dto.BudgetDto;
import com.cts.grantserve.budget_service.entity.Budget;
import com.cts.grantserve.budget_service.projection.IBudgetProjection;
import com.cts.grantserve.budget_service.service.IBudgetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@Slf4j
public class BudgetController {

    @Autowired
    private IBudgetService budgetService;

    @PostMapping("/create")
    public ResponseEntity<Long> createBudget(@RequestBody BudgetDto budgetDto) {
        return ResponseEntity.ok(budgetService.createBudget(budgetDto).budgetID());
    }

    // Get a budget by budget ID
    @GetMapping("{id}")
    public ResponseEntity<IBudgetProjection> getBudgetById(@PathVariable Long id) {
        return budgetService.getBudget(id);
    }

    // Get all budget records
    @GetMapping
    public ResponseEntity<List<IBudgetProjection>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    // Get all budget records by program IDs
    @GetMapping("/program")
    public ResponseEntity<List<IBudgetProjection>> getAllBudgetsByProgramIds(@RequestParam List<Long> programIds) {
        return ResponseEntity.ok(budgetService.getAllBudgetsByProgramIds(programIds));
    }

    // Get budget by program ID
    @GetMapping("/program/{programId}")
    public ResponseEntity<IBudgetProjection> getBudgetByProgram(@PathVariable Long programId) {
        return budgetService.getBudgetByProgram(programId);
    }

    // Update a budget's allocation
    @PatchMapping("/{budgetId}")
    public ResponseEntity<String> allocateFundToResearcher(@PathVariable Long budgetId, @RequestParam Double allocatedAmount) {
        String response = budgetService.allocateAmountToResearcherByBudgetId(budgetId, allocatedAmount);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/close/program/{id}")
    public ResponseEntity<String> closeBudgetByProgramId(@PathVariable Long id) {
        String response = budgetService.updateBudgetStatusToClosed(id);
        return ResponseEntity.ok(response);
    }

}
package com.cts.grantserve.budget_service.exception;

// Custom exception for business rule violations
public class BudgetNotModifiableException extends RuntimeException {
    public BudgetNotModifiableException(String message) {
        super(message);
    }
}
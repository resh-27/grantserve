package com.cts.grantserve.budget_service.exception;

public class InvalidProgramStatusException extends RuntimeException {
    public InvalidProgramStatusException(String message) {
        super(message);
    }
}
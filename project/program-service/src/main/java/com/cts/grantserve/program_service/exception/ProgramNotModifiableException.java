package com.cts.grantserve.program_service.exception;

// Custom exception for business rule violations
public class ProgramNotModifiableException extends RuntimeException {
    public ProgramNotModifiableException(String message) {
        super(message);
    }
}
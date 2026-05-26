package com.cts.grantserve.program_service.exception;

public class InvalidProgramStatusException extends RuntimeException {
    public InvalidProgramStatusException(String message) {
        super(message);
    }
}
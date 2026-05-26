package com.cts.grantserve.program_service.exception;

import org.springframework.http.HttpStatus;

public class BudgetClosedException extends RuntimeException {
    private final HttpStatus httpStatus;

    public BudgetClosedException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
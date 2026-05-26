package com.cts.grantserve.budget_service.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends RuntimeException {
    private final HttpStatus httpStatus;

    public InsufficientFundsException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
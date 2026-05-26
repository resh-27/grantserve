package com.cognizant.evaluation_service.exception;


import org.springframework.http.HttpStatus;

public class EvaluationNotFoundException extends RuntimeException {
    private HttpStatus httpStatus;
    public EvaluationNotFoundException(String message,HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

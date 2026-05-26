package com.cognizant.review_service.exception;


import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends RuntimeException {

    private HttpStatus httpStatus;
    public ReviewNotFoundException(String message,HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

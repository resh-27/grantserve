package com.cognizant.researcher_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter // This automatically creates the getStatus() method for you
public class ResearcherException extends RuntimeException {

    private final HttpStatus status;

    public ResearcherException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
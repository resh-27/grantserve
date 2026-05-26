package com.cognizant.researcher_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for Researcher Document related errors.
 * The @Getter annotation from Lombok automatically provides getStatus().
 */
@Getter
public class ResearcherDocumentException extends RuntimeException {

    private final HttpStatus status;

    public ResearcherDocumentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

package com.cts.grantserve.Application_Service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProposalException extends RuntimeException{
    private final HttpStatus httpStatus;
    public ProposalException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

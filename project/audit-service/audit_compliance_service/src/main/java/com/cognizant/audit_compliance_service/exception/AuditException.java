package com.cognizant.audit_compliance_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class AuditException extends RuntimeException
{
    private HttpStatus httpStatus;
    public AuditException(String message, HttpStatus httpStatus)
    {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus()
    {
        return httpStatus;
    }
}
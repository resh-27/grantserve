package com.cts.grantserve.Application_Service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class GrantApplicationException extends RuntimeException
{
    private HttpStatus httpStatus;
    public GrantApplicationException(String message, HttpStatus httpStatus)
    {
        super(message);
        this.httpStatus = httpStatus;
    }
    public HttpStatus getHttpStatus()
    {
        return httpStatus;
    }
}

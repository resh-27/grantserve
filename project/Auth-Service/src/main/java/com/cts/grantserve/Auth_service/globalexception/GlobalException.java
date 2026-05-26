package com.cts.grantserve.Auth_service.globalexception;
import com.cts.grantserve.Auth_service.exception.UserException;
import com.cts.grantserve.security.shared.exception.BaseSecurityExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ControllerAdvice
@Slf4j
public class GlobalException extends BaseSecurityExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Object> UserException(UserException u){
        return buildResponse(u.getHttpStatus(),u.getMessage());
    }

}
package com.cts.grantserve.Application_Service.globalexception;


import com.cts.grantserve.Application_Service.exception.GrantApplicationException;
import com.cts.grantserve.Application_Service.exception.ProposalException;
import com.cts.grantserve.security.shared.exception.BaseSecurityExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalException extends BaseSecurityExceptionHandler {

    @ExceptionHandler(GrantApplicationException.class)
    public ResponseEntity<Object> GrantApplicationExceptionHandler(GrantApplicationException g){
        return buildResponse(g.getHttpStatus(), g.getMessage());
    }

    @ExceptionHandler(ProposalException.class)
    public ResponseEntity<Object> proposalExceptionHandler(ProposalException p){
        // CRITICAL: Call the inherited buildResponse method
        return buildResponse(p.getHttpStatus(), p.getMessage());
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<Object> handleFeignException(feign.FeignException e) {
        log.error("Feign Client Error: Status {}, Message {}", e.status(), e.getMessage());

        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        String message = (status == HttpStatus.NOT_FOUND)
                ? "Clear Error: Invalid Request - Resource not found in dependency service."
                : "Clear Error: Communication with the dependency service failed.";
        return buildResponse(status, message);
    }
}
package com.cts.grantserve.program_service.globalexception;

import com.cts.grantserve.program_service.exception.*;
import com.cts.grantserve.security.shared.exception.BaseSecurityExceptionHandler;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import feign.FeignException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalException extends BaseSecurityExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Object> handleUserException(UserException u) {
        return buildResponse(u.getHttpStatus(), u.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return super.handleValidationExceptions(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("JSON Parse Error: {}", ex.getMessage());
        String userFriendlyMessage = "Invalid request format.";

        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            userFriendlyMessage = "Invalid value provided. Please check date formats (YYYY-MM-DD) or numeric values.";
        } else if (ex.getMessage().contains("java.time.LocalDate")) {
            userFriendlyMessage = "The date provided is invalid.";
        }

        return buildResponse(HttpStatus.BAD_REQUEST, userFriendlyMessage);
    }

    @ExceptionHandler(MysqlDataTruncation.class)
    public ResponseEntity<Object> handleMysqlDataTruncation(MysqlDataTruncation ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({ ProgramNotFoundException.class, BudgetNotFoundException.class })
    public ResponseEntity<Object> handleNotFoundException(RuntimeException ex) {
        log.error("Resource not found exception: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProgramNotModifiableException.class)
    public ResponseEntity<Object> handleProgramNotModifiableException(ProgramNotModifiableException ex) {
        log.warn("Business logic violation: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BudgetClosedException.class)
    public ResponseEntity<Object> handleBudgetClosedException(BudgetClosedException ex) {
        log.warn("Attempted action on closed budget: {}", ex.getMessage());
        return buildResponse(ex.getHttpStatus(), ex.getMessage());
    }

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<Object> handleFeign503(FeignException.ServiceUnavailable ex) {
        log.error("Downstream service returned 503: {}", ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "The requested service is temporarily unavailable. Please try again later.");
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException ex) {
        log.error("Feign error status={}, message={}", ex.status(), ex.getMessage());
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        String message = (status == HttpStatus.SERVICE_UNAVAILABLE)
                ? "The requested service is temporarily unavailable. Please try again later."
                : ex.getMessage();

        return buildResponse(status, message);
    }

    @ExceptionHandler(MaxRetriesExceededException.class)
    public ResponseEntity<Object> handleRetryExhausted(MaxRetriesExceededException ex) {
        log.error("Retry exhausted: {}", ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "The requested service unavailable after retry attempts.");
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "The requested endpoint does not exist. Please check your URL and HTTP method.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        log.error("Unexpected Error: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}
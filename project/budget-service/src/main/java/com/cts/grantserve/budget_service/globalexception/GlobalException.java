package com.cts.grantserve.budget_service.globalexception;

import com.cts.grantserve.budget_service.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@ControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> UserException(UserException u){
        return ResponseEntity.status(u.getHttpStatus()).body(u.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        log.error("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(BudgetNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(BudgetNotFoundException ex) {
        log.error("Resource not found exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BudgetNotModifiableException.class)
    public ResponseEntity<String> handleProgramNotModifiableException(BudgetNotModifiableException ex) {
        log.warn("Business logic violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientBudgetException(InsufficientFundsException ex) {
        log.error("Financial error - Insufficient funds: {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getMessage());
    }

    @ExceptionHandler(BudgetClosedException.class)
    public ResponseEntity<String> handleBudgetClosedException(BudgetClosedException ex) {
        log.warn("Attempted action on closed budget: {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "An unexpected error occurred");
        body.put("error", ex.getMessage()); // This helps you debug in Postman!

        log.error("Unexpected Error: ", ex);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<String> handlePaymentMethodEnumError(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        String errorMsg = ex.getMessage();

        // Only trigger if the error is about the PaymentMethod enum
        if (errorMsg != null && errorMsg.contains("com.cts.grantserve.enums.PaymentMethod")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid payment method. Only BANK or WALLET are accepted.");
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Please check your request format.");
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "The requested endpoint does not exist. Please check your URL and HTTP method.");
        body.put("path", ex.getResourcePath());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND); // Now it returns 404
    }

}
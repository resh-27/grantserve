package com.cts.grantserve.api_gateway.exception;

import com.cts.grantserve.security.shared.exception.BaseSecurityExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Order(-1)
@Component
public class GatewayExceptionHandler extends BaseSecurityExceptionHandler implements ErrorWebExceptionHandler {

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Check your IntelliJ Console! This print is vital.
        System.err.println("[GATEWAY DEBUG] Exception Type: " + ex.getClass().getName());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected internal error occurred.";

        if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason();
        }
        else if (ex instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Service not found in Discovery Server.";
        }
        else if (ex.getClass().getName().contains("CallNotAllowedException")) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Circuit Breaker is OPEN. Service is temporarily disabled.";
        }
        else if (ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Target service refused connection. Is it running?";
        }
        else {
            // This will now show you the actual error message in the JSON response
            message = "Gateway Error: " + ex.getMessage();
        }

        // Capture values for the Lambda
        final HttpStatus finalStatus = status;
        final String finalMessage = message;
        final ResponseEntity<Object> responseEntity = buildResponse(finalStatus, finalMessage);

        return exchange.getResponse().writeWith(Mono.fromCallable(() -> {
            exchange.getResponse().setStatusCode(finalStatus);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            // Use the objectMapper you defined to ensure valid JSON
            byte[] bytes = objectMapper.writeValueAsBytes(responseEntity.getBody());
            return exchange.getResponse().bufferFactory().wrap(bytes);
        }));
    }
}
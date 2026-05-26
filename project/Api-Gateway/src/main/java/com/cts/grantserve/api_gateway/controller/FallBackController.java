package com.cts.grantserve.api_gateway.controller;

import com.cts.grantserve.security.shared.exception.BaseSecurityExceptionHandler;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallBackController extends BaseSecurityExceptionHandler {



        @RequestMapping("/auth")

        public Mono<ResponseEntity<Object>> authFallback() {
            return Mono.just(buildResponse(HttpStatus.SERVICE_UNAVAILABLE,
                    "Auth Service is currently down. Please try again later."));
        }

        @RequestMapping("/evaluation")

        public Mono<ResponseEntity<Object>> evaluationFallback() {
            return Mono.just(buildResponse(HttpStatus.SERVICE_UNAVAILABLE,
                    "Evaluation Service is offline. We cannot process requests right now."));
        }

        @RequestMapping("/application")

        public Mono<ResponseEntity<Object>> applicationFallback() {
            return Mono.just(buildResponse(HttpStatus.GATEWAY_TIMEOUT,
                    "Application Service is taking too long to respond."));
        }
    }

package com.cognizant.researcher_service.aop;

import com.cognizant.researcher_service.client.AuditLogClient;
import com.cognizant.researcher_service.dto.AuditLogDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Autowired
    private AuditLogClient auditLogClient;

    @Around("execution(* com.cognizant.researcher_service.controller.*.*(..))")
    @CircuitBreaker(name = "auditLogCB", fallbackMethod = "fallbackAudit")
    @Retry(name = "auditLogRetry")
    public Object auditAction(ProceedingJoinPoint joinPoint) throws Throwable {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null) ? auth.getName() : "Anonymous";

        log.info("USER: {} is attempting to call controller: {}", user, joinPoint.getSignature().getName());

        // Attempting to send log to Audit Service
        auditLogClient.addLog(
                AuditLogDTO.builder()
                        .action(joinPoint.getSignature().toShortString())
                        .resource("Researcher")
                        .build()
        );

        return joinPoint.proceed();
    }

    // Fallback method to ensure Researcher Service keeps working if Audit Service is down
    public Object fallbackAudit(ProceedingJoinPoint joinPoint, Throwable t) throws Throwable {
        log.error("Audit Service unavailable! Falling back for method: {}. Error: {}",
                joinPoint.getSignature().getName(), t.getMessage());
        return joinPoint.proceed();
    }
}
package com.cts.grantserve.budget_service.aop;

import com.cts.grantserve.budget_service.client.AuditLogClient;
import com.cts.grantserve.budget_service.dto.AuditLogDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Autowired
    AuditLogClient auditLogClient;

    @Around("execution(* com.cts.grantserve.budget_service.controller.*.*(..))")
    @CircuitBreaker(name = "auditLogCB")
    @Retry(name = "auditLogRetry")
    public Object auditAction(ProceedingJoinPoint joinPoint) throws Throwable {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null) ? auth.getName() : "Anonymous";

        log.info("USER: {} is attempting to call: {}", user, joinPoint.getSignature().getName());

        auditLogClient.addLog(
                AuditLogDTO
                        .builder()
                        .action(joinPoint.getSignature().toShortString())
                        .resource("Budget")
                        .build()
        );

        return joinPoint.proceed();
    }
}
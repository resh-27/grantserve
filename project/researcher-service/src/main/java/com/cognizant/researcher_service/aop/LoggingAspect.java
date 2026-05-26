package com.cognizant.researcher_service.aop;

import com.cognizant.researcher_service.client.AuditLogClient;
import com.cognizant.researcher_service.dto.AuditLogDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Autowired
    private AuditLogClient auditLogClient;

    @Pointcut("execution(* com.cognizant.researcher_service.service.*.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    @CircuitBreaker(name = "auditLogCB", fallbackMethod = "fallbackLogging")
    @Retry(name = "auditLogRetry")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long timeTaken = System.currentTimeMillis() - startTime;

        log.info("Method {} executed in {} ms",
                joinPoint.getSignature().toShortString(),
                timeTaken);

        auditLogClient.addLog(
                AuditLogDTO.builder()
                        .action("PERFORMANCE: " + joinPoint.getSignature().toShortString() + " [" + timeTaken + "ms]")
                        .resource("Researcher")
                        .build()
        );

        return result;
    }

    // Fallback for Logging Aspect
    public Object fallbackLogging(ProceedingJoinPoint joinPoint, Throwable t) throws Throwable {
        log.warn("Logging Aspect Fallback: Could not send performance log for {}. Proceeding normally.",
                joinPoint.getSignature().getName());
        return joinPoint.proceed();
    }
}
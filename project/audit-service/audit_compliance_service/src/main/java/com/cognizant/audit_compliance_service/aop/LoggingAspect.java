package com.cognizant.audit_compliance_service.aop;

import com.cognizant.audit_compliance_service.client.AuditLogClient;
import com.cognizant.audit_compliance_service.dto.AuditLogDTO;
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

    // Define a pointcut to target all methods in your service package
    @Pointcut("execution(* com.cts.grantserve.program_service.service.*.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    @CircuitBreaker(name = "auditLogCB")
    @Retry(name = "auditLogRetry")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Proceed with the actual method execution
        Object result = joinPoint.proceed();

        long timeTaken = System.currentTimeMillis() - startTime;

        log.info("Method {} executed in {} ms",
                joinPoint.getSignature().toShortString(),
                timeTaken);

        auditLogClient.addLog(
                AuditLogDTO
                        .builder()
                        .action(joinPoint.getSignature().toShortString())
                        .resource("Program")
                        .build()
        );

        return result;
    }
}
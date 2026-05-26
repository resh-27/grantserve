package com.cts.grantserve.audit_log_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Around("execution(* com.cts.grantserve.audit_log_service.controller.*.*(..))")
    public Object auditAction(ProceedingJoinPoint joinPoint) throws Throwable {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null) ? auth.getName() : "Anonymous";

        log.info("USER: {} is attempting to call: {}", user, joinPoint.getSignature().getName());
//        System.out.println(org.springframework.security.core.context.SecurityContextHolder.getContext());

        return joinPoint.proceed();
    }
}
package com.cognizant.audit_compliance_service.aop;

import com.cognizant.audit_compliance_service.client.ResearcherClient;
import com.cognizant.audit_compliance_service.dto.AuditLogDTO;
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
    private ResearcherClient researcherClient;

    @Around("execution(* com.cognizant.audit_compliance_service.controller.ComplianceRecordController.*(..))")
    public Object auditAction(ProceedingJoinPoint joinPoint) throws Throwable {
        // Capture user from security context
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null) ? auth.getName() : "Anonymous";

        log.info("USER: {} is calling: {}", user, joinPoint.getSignature().getName());

        // Since AuditLogDTO lacks a username field, we combine user + action
        String combinedAction = String.format("User: %s | Action: %s",
                user,
                joinPoint.getSignature().toShortString());

        researcherClient.addLog(
                AuditLogDTO.builder()
                        .action(combinedAction) // Valid field
                        .resource("Compliance-Service") // Valid field
                        .build()
        );

        return joinPoint.proceed();
    }
}
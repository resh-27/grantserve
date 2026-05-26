package com.cts.grantserve.audit_log_service.service;

import com.cts.grantserve.audit_log_service.dto.AuditLogDTO;
import com.cts.grantserve.audit_log_service.entity.AuditLog;
import com.cts.grantserve.audit_log_service.projection.IAuditLogProjection;
import com.cts.grantserve.audit_log_service.repository.AuditLogRepository;
import com.cts.grantserve.audit_log_service.specification.AuditLogSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements IAuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void addLog(AuditLogDTO dto) {

        String userID = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // Get ID

        AuditLog log = AuditLog.builder()
                .userID(userID)
                .action(dto.getAction())
                .resource(dto.getResource())
                .build();

        auditLogRepository.save(log);
    }

    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<IAuditLogProjection> searchLogsWithProjection(String resource, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Specification<AuditLog> spec = Specification
                .where(AuditLogSpecifications.hasResource(resource))
                .and(AuditLogSpecifications.isWithinTimestamp(start, end));

        return auditLogRepository.findBy(
            spec,
            query -> query.as(IAuditLogProjection.class).page(pageable)
        );
    }

}
package com.cts.grantserve.audit_log_service.service;

import com.cts.grantserve.audit_log_service.dto.AuditLogDTO;
import com.cts.grantserve.audit_log_service.entity.AuditLog;
import com.cts.grantserve.audit_log_service.projection.IAuditLogProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IAuditLogService {
    void addLog(AuditLogDTO dto);
    Page<AuditLog> getAllLogs(Pageable pageable);
    Page<IAuditLogProjection> searchLogsWithProjection(String resource, LocalDateTime start, LocalDateTime end, Pageable pageable);
}

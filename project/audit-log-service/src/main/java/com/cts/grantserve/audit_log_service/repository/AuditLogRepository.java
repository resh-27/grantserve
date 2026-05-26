package com.cts.grantserve.audit_log_service.repository;

import com.cts.grantserve.audit_log_service.entity.AuditLog;
import com.cts.grantserve.audit_log_service.projection.IAuditLogProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
}
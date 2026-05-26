package com.cognizant.audit_compliance_service.repository;

import com.cognizant.audit_compliance_service.entity.Audit;
import com.cognizant.audit_compliance_service.enums.AuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditRepository extends JpaRepository<Audit, Long> {
    List<Audit> findByStatus(AuditStatus status);
}

package com.cognizant.audit_compliance_service.repository;

import com.cognizant.audit_compliance_service.entity.ComplianceRecord;
import com.cognizant.audit_compliance_service.enums.ComplianceResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    List<ComplianceRecord> findByResult(ComplianceResult result);
}

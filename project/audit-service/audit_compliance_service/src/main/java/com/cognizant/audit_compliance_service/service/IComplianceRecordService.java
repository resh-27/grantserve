package com.cognizant.audit_compliance_service.service;

import com.cognizant.audit_compliance_service.dto.ComplianceRecordDto;
import com.cognizant.audit_compliance_service.entity.ComplianceRecord;
import com.cognizant.audit_compliance_service.enums.ComplianceResult;
import com.cognizant.audit_compliance_service.exception.ComplianceRecordException;

import java.util.List;
import java.util.Optional;

public interface IComplianceRecordService {

    String createComplianceRecord(ComplianceRecordDto complianceRecordDto) throws ComplianceRecordException;

    String deleteComplianceRecord(int id) throws ComplianceRecordException;

    List<ComplianceRecord> getAllComplianceRecord();

    Optional<ComplianceRecord> getComplianceRecord(int id);

    List<ComplianceRecord> getComplianceRecordByResult(ComplianceResult result);

    Optional<ComplianceRecord> updateComplianceRecordResult(int id, ComplianceResult result);
}
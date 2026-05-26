package com.cognizant.audit_compliance_service.service;

import com.cognizant.audit_compliance_service.dto.AuditDto;
import com.cognizant.audit_compliance_service.entity.Audit;
import com.cognizant.audit_compliance_service.enums.AuditStatus;
import com.cognizant.audit_compliance_service.exception.AuditException;

import java.util.List;
import java.util.Optional;

public interface IAuditService {

    String createAudit(AuditDto auditDto) throws AuditException;

    String deleteAudit(int id) throws AuditException;

    List<Audit> getAllAudits();

    Optional<Audit> getAudit(int id);

    List<Audit> getAuditByStatus(AuditStatus status);

    Optional<Audit> updateAuditStatus(int id, AuditStatus status);
}
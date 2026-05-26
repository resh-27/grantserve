package com.cognizant.audit_compliance_service.service;

import com.cognizant.audit_compliance_service.dto.AuditDto;
import com.cognizant.audit_compliance_service.entity.Audit;
import com.cognizant.audit_compliance_service.enums.AuditStatus;
import com.cognizant.audit_compliance_service.exception.AuditException;
import com.cognizant.audit_compliance_service.repository.AuditRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AuditServiceImpl implements IAuditService{
    @Autowired
    AuditRepository auditRepository;

    @Override
    public String createAudit(AuditDto auditDto) throws AuditException {
        Audit audit = new Audit();
        BeanUtils.copyProperties(auditDto, audit);
        audit.setDate(LocalDate.now());
        audit.setStatus(AuditStatus.PENDING);
        auditRepository.save(audit);
        return "Created SuccessFully";
    }

    @Override
    public String deleteAudit(int id) throws  AuditException{
        auditRepository.deleteById((long) id);
        return "Deleted SuccessFully";
    }

    @Override
    public List<Audit> getAllAudits() {
        return auditRepository.findAll();
    }

    @Override
    public Optional<Audit> getAudit(int id) {
        return auditRepository.findById((long) id);
    }

    @Override
    public List<Audit> getAuditByStatus(AuditStatus status) {
        return auditRepository.findByStatus(status);
    }

    @Override
    public Optional<Audit> updateAuditStatus(int id, AuditStatus status) {
        return auditRepository.findById((long)id).map(audit -> {
            audit.setStatus(status);
            return auditRepository.save(audit);
        });
    }
}
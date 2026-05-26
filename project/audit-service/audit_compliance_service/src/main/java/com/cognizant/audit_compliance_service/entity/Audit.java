package com.cognizant.audit_compliance_service.entity;

import com.cognizant.audit_compliance_service.enums.AuditScope;
import com.cognizant.audit_compliance_service.enums.AuditStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditID;
    private Long officerID;

    @Enumerated(EnumType.STRING)
    private AuditScope scope;
    private String findings;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    public Long getAuditID() {
        return auditID;
    }

    public void setAuditID(Long auditID) {
        this.auditID = auditID;
    }

    public Long getOfficerID() {
        return officerID;
    }

    public void setOfficerID(Long officerID) {
        this.officerID = officerID;
    }

    public AuditScope getScope() {
        return scope;
    }

    public void setScope(AuditScope scope) {
        this.scope = scope;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }
}
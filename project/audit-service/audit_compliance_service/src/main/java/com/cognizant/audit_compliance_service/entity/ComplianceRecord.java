package com.cognizant.audit_compliance_service.entity;

import com.cognizant.audit_compliance_service.enums.ComplianceResult;
import com.cognizant.audit_compliance_service.enums.ComplianceType;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name="Compliance_record")
public class ComplianceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complianceID;

    private Long entityID; // Can be ApplicationID or ProgramID

    @Enumerated(EnumType.STRING)
    private ComplianceType type;

    @Enumerated(EnumType.STRING)
    private ComplianceResult result;

    private LocalDate date;
    private String notes;

    public Long getComplianceID() {
        return complianceID;
    }

    public void setComplianceID(Long complianceID) {
        this.complianceID = complianceID;
    }

    public Long getEntityID() {
        return entityID;
    }

    public void setEntityID(Long entityID) {
        this.entityID = entityID;
    }

    public ComplianceType getType() {
        return type;
    }

    public void setType(ComplianceType type) {
        this.type = type;
    }

    public ComplianceResult getResult() {
        return result;
    }

    public void setResult(ComplianceResult result) {
        this.result = result;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
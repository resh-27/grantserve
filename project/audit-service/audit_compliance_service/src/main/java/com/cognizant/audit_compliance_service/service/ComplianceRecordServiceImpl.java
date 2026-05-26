package com.cognizant.audit_compliance_service.service;

import com.cognizant.audit_compliance_service.dto.ComplianceRecordDto;
import com.cognizant.audit_compliance_service.entity.ComplianceRecord;
import com.cognizant.audit_compliance_service.enums.ComplianceResult;
import com.cognizant.audit_compliance_service.exception.ComplianceRecordException;
import com.cognizant.audit_compliance_service.feign.ResearcherDocumentClient;
import com.cognizant.audit_compliance_service.repository.ComplianceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplianceRecordServiceImpl implements IComplianceRecordService{

    private final ComplianceRecordRepository complianceRecordRepository;
    private final ResearcherDocumentClient researcherDocumentClient;

    @Override
    public String createComplianceRecord(ComplianceRecordDto complianceRecordDto) throws ComplianceRecordException {
        ComplianceRecord complianceRecord = new ComplianceRecord();
        BeanUtils.copyProperties(complianceRecordDto, complianceRecord);
        complianceRecord.setDate(LocalDate.now());
        complianceRecord.setResult(ComplianceResult.PENDING);
        complianceRecordRepository.save(complianceRecord);
        return "Created SuccessFully";
    }

    @Override
    public String deleteComplianceRecord(int id) throws  ComplianceRecordException{
        complianceRecordRepository.deleteById((long) id);
        return "Deleted SuccessFully";
    }

    @Override
    public List<ComplianceRecord> getAllComplianceRecord() {
        return complianceRecordRepository.findAll();
    }

    @Override
    public Optional<ComplianceRecord> getComplianceRecord(int id) {
        return complianceRecordRepository.findById((long) id);
    }

    @Override
    public List<ComplianceRecord> getComplianceRecordByResult(ComplianceResult result) {
        return complianceRecordRepository.findByResult(result);
    }

    @Override
    public Optional<ComplianceRecord> updateComplianceRecordResult(int id, ComplianceResult result) {
        return complianceRecordRepository.findById((long)id).map(complianceRecord -> {
            complianceRecord.setResult(result);
            return complianceRecordRepository.save(complianceRecord);
        });
    }

    public String approveDocument(Long docId) {
        String response = researcherDocumentClient.updateStatus(docId, "Approved");
        System.out.println(response);
        return response;
    }
}
package com.cognizant.audit_compliance_service.controller;

import com.cognizant.audit_compliance_service.dto.ComplianceRecordDto;
import com.cognizant.audit_compliance_service.entity.ComplianceRecord;
import com.cognizant.audit_compliance_service.enums.ComplianceResult;
import com.cognizant.audit_compliance_service.service.ComplianceRecordServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/GrantServe")
public class ComplianceRecordController {

    @Autowired
    ComplianceRecordServiceImpl complianceRecordService;

    @PostMapping("/createComplianceRecord")
    public ResponseEntity<String> createComplianceRecord(@Valid @RequestBody ComplianceRecordDto complianceRecord){
        return ResponseEntity.status(HttpStatus.CREATED).body(complianceRecordService.createComplianceRecord(complianceRecord));
    }

    @DeleteMapping("/DeleteComplianceRecord/{id}")
    public ResponseEntity<String> DeleteComplianceRecord(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(complianceRecordService.deleteComplianceRecord(id));
    }

    @GetMapping("/complianceRecords")
    public ResponseEntity<?> getAllComplianceRecord() {
        return ResponseEntity.ok(complianceRecordService.getAllComplianceRecord());
    }

    @GetMapping("getComplianceRecord/{id}")
    public ResponseEntity<ComplianceRecord> getComplianceRecord(@PathVariable int id) {
        return complianceRecordService.getComplianceRecord(id)
                .map(app -> ResponseEntity.ok(app))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("getComplianceRecordByResult/{result}")
    public ResponseEntity<?> getComplianceRecordByResult(@PathVariable ComplianceResult result) {
        return ResponseEntity.ok(complianceRecordService.getComplianceRecordByResult(result));
    }

    @PatchMapping("/updateComplianceRecordResult/{id}")
    public ResponseEntity<ComplianceRecord> updateComplianceRecordResult(@PathVariable int id, @Valid @RequestBody ComplianceRecord complianceRecord) {
        return complianceRecordService.updateComplianceRecordResult(id, complianceRecord.getResult())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compliance Record not found with id " + id));
    }

    @PutMapping("/approveDocument/{docId}")
    public ResponseEntity<String> approveDocument(@PathVariable Long docId) {
        String result = complianceRecordService.approveDocument(docId);
        return ResponseEntity.ok(result);
    }
}
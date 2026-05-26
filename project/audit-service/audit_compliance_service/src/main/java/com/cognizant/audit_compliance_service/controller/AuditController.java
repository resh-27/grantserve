package com.cognizant.audit_compliance_service.controller;

import com.cognizant.audit_compliance_service.dto.AuditDto;
import com.cognizant.audit_compliance_service.entity.Audit;
import com.cognizant.audit_compliance_service.enums.AuditStatus;
import com.cognizant.audit_compliance_service.service.AuditServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/GrantServe")
public class AuditController {

    @Autowired
    private AuditServiceImpl auditService;

    @PostMapping("/createAudit")
    public ResponseEntity<String> createAudit(@Valid @RequestBody AuditDto audit){
        return ResponseEntity.status(HttpStatus.CREATED).body(auditService.createAudit(audit));
    }

    @DeleteMapping("/deleteAudit/{id}")
    public ResponseEntity<String> DeleteAudit(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(auditService.deleteAudit(id));
    }

    @GetMapping("/audits")
    public ResponseEntity<?> getAllAudits() {
        return ResponseEntity.ok(auditService.getAllAudits());
    }

    @GetMapping("/getAudit/{id}")
    public ResponseEntity<Audit> getAudit(@PathVariable int id) {
        return auditService.getAudit(id)
                .map(app -> ResponseEntity.ok(app))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getAuditByStatus/{status}")
    public ResponseEntity<?> getAuditByStatus(@PathVariable AuditStatus status) {
        return ResponseEntity.ok(auditService.getAuditByStatus(status));
    }

    @PatchMapping("/updateAuditStatus/{id}")
    public ResponseEntity<Audit> updateAuditStatus(@PathVariable int id, @Valid @RequestBody Audit audit) {
        return auditService.updateAuditStatus(id, audit.getStatus())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Audit not found with id " + id));
    }
}
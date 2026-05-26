package com.cts.grantserve.audit_log_service.controller;

import com.cts.grantserve.audit_log_service.dto.AuditLogDTO;
import com.cts.grantserve.audit_log_service.entity.AuditLog;
import com.cts.grantserve.audit_log_service.projection.IAuditLogProjection;
import com.cts.grantserve.audit_log_service.service.AuditLogServiceImpl;
import com.cts.grantserve.audit_log_service.service.IAuditLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/audit-log")
public class AuditLogController {

    @Autowired
    private IAuditLogService auditLogService;

    // Open to internal microservices to log actions
    @PostMapping("/add")
    public ResponseEntity<Void> addLog(@Valid @RequestBody AuditLogDTO logDTO) {
        auditLogService.addLog(logDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Restricted to Admin and Compliance Officer based on SRS roles
    @GetMapping("/all")
    public ResponseEntity<PagedModel<AuditLog>> getAllLogs(Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getAllLogs(pageable);
        return ResponseEntity.ok(new PagedModel<>(logs));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedModel<IAuditLogProjection>> searchSummary(
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(new PagedModel<>(auditLogService.searchLogsWithProjection(resource, start, end, pageable)));
    }

}
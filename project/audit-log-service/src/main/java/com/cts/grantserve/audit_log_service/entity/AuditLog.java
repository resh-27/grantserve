package com.cts.grantserve.audit_log_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "AuditLog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditID;

    @Column(nullable = false, updatable = false, length = 100)
    private String userID;

    @Column(nullable = false,  updatable = false,  length = 100)
    private String action; // e.g., "CREATE", "UPDATE", "DELETE", "VIEW"

    @Column(nullable = false, updatable = false, length = 100)
    private String resource; // e.g., "GrantApplication", "Disbursement"

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
package com.cts.grantserve.audit_log_service.specification;// Create a new file: AuditLogSpecifications.java

import com.cts.grantserve.audit_log_service.entity.AuditLog;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class AuditLogSpecifications {

    public static Specification<AuditLog> hasResource(String resource) {
        return (root, query, cb) -> 
            resource == null ? cb.conjunction() : cb.equal(root.get("resource"), resource);
    }

    public static Specification<AuditLog> isWithinTimestamp(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null)
                return cb.conjunction();
            if (start != null && end != null)
                return cb.between(root.get("timestamp"), start, end);
            if (start != null)
                return cb.greaterThanOrEqualTo(root.get("timestamp"), start);
            return cb.lessThanOrEqualTo(root.get("timestamp"), end);
        };
    }
}
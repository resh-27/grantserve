package com.cts.grantserve.audit_log_service.projection;

import java.time.LocalDateTime;

public interface IAuditLogProjection {
    String getUserID();
    String getAction();
    String getResource();
    LocalDateTime getTimestamp();
}
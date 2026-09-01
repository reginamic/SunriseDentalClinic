package com.sunrisedental.api.pattern.bridge;

import com.sunrisedental.api.model.AuditLog;

import java.sql.SQLException;

/**
 * Implementor side of the Bridge pattern.
 *
 * AuditLogger abstractions will use this interface without
 * depending on a particular audit-storage mechanism.
 *
 * Different implementations may persist audit records to:
 *
 * - MySQL
 * - console output
 * - another external logging destination
 *
 * without changing the audit abstraction itself.
 */
public interface AuditLogWriter {

    void write(
            AuditLog auditLog)
            throws SQLException;
}
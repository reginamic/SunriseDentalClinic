package com.sunrisedental.api.pattern.bridge;

import com.sunrisedental.api.model.AuditLog;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstraction side of the Bridge pattern.
 *
 * AuditLogger defines high-level audit behaviour while delegating
 * the actual persistence mechanism to AuditLogWriter.
 *
 * Therefore, audit categories and audit destinations can evolve
 * independently.
 */
public abstract class AuditLogger {

    protected final AuditLogWriter auditLogWriter;

    protected AuditLogger(
            AuditLogWriter auditLogWriter) {

        this.auditLogWriter =
                Objects.requireNonNull(
                        auditLogWriter,
                        "AuditLogWriter cannot be null."
                );
    }

    /**
     * Creates a standard audit record and delegates persistence
     * to the Bridge Implementor.
     */
    protected void writeAudit(
            Integer userId,
            String actionType,
            String entityType,
            Integer entityId,
            String description,
            String ipAddress)
            throws SQLException {

        validateActionType(
                actionType
        );

        AuditLog auditLog =
                new AuditLog();

        auditLog.setUserId(
                userId
        );

        auditLog.setActionType(
                actionType.trim()
                        .toUpperCase()
        );

        auditLog.setEntityType(
                normalizeNullable(
                        entityType
                )
        );

        auditLog.setEntityId(
                entityId
        );

        auditLog.setDescription(
                normalizeNullable(
                        description
                )
        );

        auditLog.setIpAddress(
                normalizeNullable(
                        ipAddress
                )
        );

        auditLog.setCreatedAt(
                LocalDateTime.now()
        );

        /*
         * BRIDGE:
         *
         * AuditLogger does not know whether this record is being
         * written to MySQL, console output or another destination.
         *
         * That responsibility belongs to AuditLogWriter.
         */
        auditLogWriter.write(
                auditLog
        );
    }

    private void validateActionType(
            String actionType) {

        if (actionType == null
                || actionType.isBlank()) {

            throw new IllegalArgumentException(
                    "Audit action type is required."
            );
        }

        if (actionType.trim().length() > 50) {

            throw new IllegalArgumentException(
                    "Audit action type cannot exceed 50 characters."
            );
        }
    }

    private String normalizeNullable(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}
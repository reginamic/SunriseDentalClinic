package com.sunrisedental.api.pattern.bridge;

import com.sunrisedental.api.model.AuditLog;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Alternative Concrete Implementor of the Bridge pattern.
 *
 * Writes audit information to the application console instead
 * of MySQL. This demonstrates that the audit abstraction can
 * use a different output mechanism without being modified.
 */
public class ConsoleAuditLogWriter
        implements AuditLogWriter {

    @Override
    public void write(
            AuditLog auditLog)
            throws SQLException {

        if (auditLog == null) {

            throw new IllegalArgumentException(
                    "Audit log is required."
            );
        }

        LocalDateTime timestamp =
                auditLog.getCreatedAt() != null
                        ? auditLog.getCreatedAt()
                        : LocalDateTime.now();

        System.out.println(
                "[SUNRISE-AUDIT]"
                + " time=" + timestamp
                + " userId=" + valueOrDash(
                        auditLog.getUserId()
                )
                + " action=" + valueOrDash(
                        auditLog.getActionType()
                )
                + " entity=" + valueOrDash(
                        auditLog.getEntityType()
                )
                + " entityId=" + valueOrDash(
                        auditLog.getEntityId()
                )
                + " ip=" + valueOrDash(
                        auditLog.getIpAddress()
                )
                + " description=\""
                + safeDescription(
                        auditLog.getDescription()
                )
                + "\""
        );
    }

    private String valueOrDash(
            Object value) {

        return value == null
                ? "-"
                : String.valueOf(value);
    }

    private String safeDescription(
            String description) {

        if (description == null
                || description.isBlank()) {

            return "-";
        }

        /*
         * Keep console output on one line.
         */
        return description
                .replace(
                        "\r",
                        " "
                )
                .replace(
                        "\n",
                        " "
                )
                .trim();
    }
}
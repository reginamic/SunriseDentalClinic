package com.sunrisedental.api.pattern.bridge;

import java.sql.SQLException;

/**
 * Concrete Abstraction in the Bridge pattern.
 *
 * SecurityAuditLogger defines audit operations related to
 * authentication and session security.
 *
 * It does not know how audit records are stored. Persistence
 * is delegated to the AuditLogWriter implementation supplied
 * to the parent AuditLogger.
 */
public class SecurityAuditLogger
        extends AuditLogger {

    public SecurityAuditLogger(
            AuditLogWriter auditLogWriter) {

        super(
                auditLogWriter
        );
    }

    /**
     * Records a successful staff login.
     */
    public void logLoginSuccess(
            Integer userId,
            String username,
            String ipAddress)
            throws SQLException {

        writeAudit(
                userId,
                "LOGIN_SUCCESS",
                "USER",
                userId,
                "Successful login for user: "
                        + safeUsername(
                                username
                        ),
                ipAddress
        );
    }

    /**
     * Records an unsuccessful login attempt.
     *
     * The user ID may be unknown because authentication
     * did not succeed.
     */
    public void logLoginFailure(
            String username,
            String ipAddress)
            throws SQLException {

        writeAudit(
                null,
                "LOGIN_FAILURE",
                "USER",
                null,
                "Failed login attempt for username: "
                        + safeUsername(
                                username
                        ),
                ipAddress
        );
    }

    /**
     * Records a staff logout.
     */
    public void logLogout(
            Integer userId,
            String username,
            String ipAddress)
            throws SQLException {

        writeAudit(
                userId,
                "LOGOUT",
                "USER",
                userId,
                "User logged out: "
                        + safeUsername(
                                username
                        ),
                ipAddress
        );
    }

    private String safeUsername(
            String username) {

        if (username == null
                || username.isBlank()) {

            return "unknown";
        }

        return username.trim();
    }
}
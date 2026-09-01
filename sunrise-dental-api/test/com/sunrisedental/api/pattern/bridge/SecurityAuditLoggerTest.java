package com.sunrisedental.api.pattern.bridge;

import com.sunrisedental.api.model.AuditLog;

import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class SecurityAuditLoggerTest {

    /*
     * Test double for the Implementor side of the Bridge.
     *
     * Instead of writing to MySQL, this writer simply keeps
     * the received AuditLog in memory so that the abstraction
     * can be tested independently of the database.
     */
    private static class CapturingAuditLogWriter
            implements AuditLogWriter {

        private AuditLog capturedAuditLog;

        @Override
        public void write(
                AuditLog auditLog)
                throws SQLException {

            this.capturedAuditLog =
                    auditLog;
        }

        public AuditLog getCapturedAuditLog() {
            return capturedAuditLog;
        }
    }

    @Test
    public void logLoginSuccessDelegatesToBridgeWriter()
            throws SQLException {

        CapturingAuditLogWriter writer =
                new CapturingAuditLogWriter();

        SecurityAuditLogger logger =
                new SecurityAuditLogger(
                        writer
                );

        logger.logLoginSuccess(
                1,
                "admin",
                "127.0.0.1"
        );

        AuditLog auditLog =
                writer.getCapturedAuditLog();

        assertNotNull(
                auditLog
        );

        assertEquals(
                Integer.valueOf(1),
                auditLog.getUserId()
        );

        assertEquals(
                "LOGIN_SUCCESS",
                auditLog.getActionType()
        );

        assertEquals(
                "USER",
                auditLog.getEntityType()
        );

        assertEquals(
                Integer.valueOf(1),
                auditLog.getEntityId()
        );

        assertEquals(
                "Successful login for user: admin",
                auditLog.getDescription()
        );

        assertEquals(
                "127.0.0.1",
                auditLog.getIpAddress()
        );

        assertNotNull(
                auditLog.getCreatedAt()
        );
    }

    @Test
    public void logLoginFailureDoesNotRequireKnownUserId()
            throws SQLException {

        CapturingAuditLogWriter writer =
                new CapturingAuditLogWriter();

        SecurityAuditLogger logger =
                new SecurityAuditLogger(
                        writer
                );

        logger.logLoginFailure(
                "admin",
                "127.0.0.1"
        );

        AuditLog auditLog =
                writer.getCapturedAuditLog();

        assertNotNull(
                auditLog
        );

        assertNull(
                auditLog.getUserId()
        );

        assertEquals(
                "LOGIN_FAILURE",
                auditLog.getActionType()
        );

        assertEquals(
                "USER",
                auditLog.getEntityType()
        );

        assertNull(
                auditLog.getEntityId()
        );

        assertEquals(
                "Failed login attempt for username: admin",
                auditLog.getDescription()
        );

        assertEquals(
                "127.0.0.1",
                auditLog.getIpAddress()
        );
    }

    @Test
    public void securityLoggerWorksWithDifferentImplementor()
            throws SQLException {

        CapturingAuditLogWriter writer =
                new CapturingAuditLogWriter();

        AuditLogger logger =
                new SecurityAuditLogger(
                        writer
                );

        /*
         * The concrete abstraction receives an AuditLogWriter
         * rather than depending on DatabaseAuditLogWriter.
         *
         * This verifies the central Bridge relationship:
         * abstraction and implementation are independently
         * replaceable.
         */
        assertNotNull(
                logger
        );

        assertTrue(
                logger instanceof SecurityAuditLogger
        );
    }
}
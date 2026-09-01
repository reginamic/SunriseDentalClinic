package com.sunrisedental.api.pattern.bridge;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Concrete Implementor of the Bridge pattern.
 *
 * Persists audit records to the MySQL audit_logs table.
 */
public class DatabaseAuditLogWriter
        implements AuditLogWriter {

    private final DatabaseConnectionManager
            connectionManager;

    public DatabaseAuditLogWriter() {

        this.connectionManager =
                DatabaseConnectionManager
                        .getInstance();
    }

    @Override
    public void write(
            AuditLog auditLog)
            throws SQLException {

        if (auditLog == null) {

            throw new IllegalArgumentException(
                    "Audit log is required."
            );
        }

        String sql =
                """
                INSERT INTO audit_logs
                (
                    user_id,
                    action_type,
                    entity_type,
                    entity_id,
                    description,
                    ip_address
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        connectionManager
                                .getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        )
        ) {

            /*
             * Nullable USER ID
             */
            if (auditLog.getUserId() == null) {

                statement.setNull(
                        1,
                        java.sql.Types.INTEGER
                );

            } else {

                statement.setInt(
                        1,
                        auditLog.getUserId()
                );
            }

            statement.setString(
                    2,
                    auditLog.getActionType()
            );

            /*
             * Nullable ENTITY TYPE
             */
            if (auditLog.getEntityType() == null
                    || auditLog
                            .getEntityType()
                            .isBlank()) {

                statement.setNull(
                        3,
                        java.sql.Types.VARCHAR
                );

            } else {

                statement.setString(
                        3,
                        auditLog.getEntityType()
                );
            }

            /*
             * Nullable ENTITY ID
             */
            if (auditLog.getEntityId() == null) {

                statement.setNull(
                        4,
                        java.sql.Types.INTEGER
                );

            } else {

                statement.setInt(
                        4,
                        auditLog.getEntityId()
                );
            }

            /*
             * Nullable DESCRIPTION
             */
            if (auditLog.getDescription() == null
                    || auditLog
                            .getDescription()
                            .isBlank()) {

                statement.setNull(
                        5,
                        java.sql.Types.VARCHAR
                );

            } else {

                statement.setString(
                        5,
                        auditLog.getDescription()
                );
            }

            /*
             * Nullable IP ADDRESS
             */
            if (auditLog.getIpAddress() == null
                    || auditLog
                            .getIpAddress()
                            .isBlank()) {

                statement.setNull(
                        6,
                        java.sql.Types.VARCHAR
                );

            } else {

                statement.setString(
                        6,
                        auditLog.getIpAddress()
                );
            }

            statement.executeUpdate();
        }
    }
}
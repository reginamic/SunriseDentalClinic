package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.pattern.memento.AppointmentMemento;
import com.sunrisedental.api.repository.AppointmentHistoryRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

public class JdbcAppointmentHistoryRepository
        implements AppointmentHistoryRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcAppointmentHistoryRepository() {

        this.connectionManager =
                DatabaseConnectionManager.getInstance();
    }

    @Override
    public void save(
            AppointmentMemento memento,
            int changedBy,
            String changeType)
            throws SQLException {

        /*
         * Basic defensive validation.
         */
        if (memento == null) {

            throw new IllegalArgumentException(
                    "Appointment history snapshot is required."
            );
        }

        if (memento.getAppointmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Valid appointment ID is required for history."
            );
        }

        if (changedBy <= 0) {

            throw new IllegalArgumentException(
                    "Valid user ID is required for appointment history."
            );
        }

        if (changeType == null
                || changeType.isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment history change type is required."
            );
        }

        String normalizedChangeType =
                changeType
                        .trim()
                        .toUpperCase();

        /*
         * The current appointment workflow records only
         * normal updates/reschedules and cancellations.
         */
        if (!normalizedChangeType.equals("UPDATE")
                && !normalizedChangeType.equals("CANCEL")) {

            throw new IllegalArgumentException(
                    "Appointment history change type must be "
                    + "UPDATE or CANCEL."
            );
        }

        String sql = """
                INSERT INTO appointment_history
                (
                    appointment_id,
                    appointment_number,
                    patient_id,
                    dentist_id,
                    treatment_id,
                    appointment_date,
                    appointment_time,
                    status,
                    notes,
                    changed_by,
                    change_type
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    memento.getAppointmentId()
            );

            statement.setString(
                    2,
                    memento.getAppointmentNumber()
            );

            statement.setInt(
                    3,
                    memento.getPatientId()
            );

            statement.setInt(
                    4,
                    memento.getDentistId()
            );

            statement.setInt(
                    5,
                    memento.getTreatmentId()
            );

            statement.setDate(
                    6,
                    Date.valueOf(
                            memento.getAppointmentDate()
                    )
            );

            statement.setTime(
                    7,
                    Time.valueOf(
                            memento.getAppointmentTime()
                    )
            );

            if (memento.getStatus() == null) {

                statement.setString(
                        8,
                        null
                );

            } else {

                statement.setString(
                        8,
                        memento
                                .getStatus()
                                .name()
                                .toLowerCase()
                );
            }

            statement.setString(
                    9,
                    memento.getNotes()
            );

            statement.setInt(
                    10,
                    changedBy
            );

            statement.setString(
                    11,
                    normalizedChangeType
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows != 1) {

                throw new SQLException(
                        "Appointment history was not recorded correctly."
                );
            }
        }
    }
}
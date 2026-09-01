package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.AppointmentHistoryRecord;
import com.sunrisedental.api.pattern.memento.AppointmentMemento;
import com.sunrisedental.api.repository.AppointmentHistoryRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class JdbcAppointmentHistoryRepository
        implements AppointmentHistoryRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcAppointmentHistoryRepository() {

        this.connectionManager =
                DatabaseConnectionManager.getInstance();
    }


    // =========================================================
    // SAVE HISTORY
    // =========================================================

    @Override
    public void save(
            AppointmentMemento memento,
            int changedBy,
            String changeType)
            throws SQLException {

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


        if (!normalizedChangeType.equals("UPDATE")
                && !normalizedChangeType.equals("CANCEL")) {

            throw new IllegalArgumentException(
                    "Appointment history change type must be "
                    + "UPDATE or CANCEL."
            );
        }


        String sql =
                """
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
                        memento.getStatus().name()
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


    // =========================================================
    // READ HISTORY
    // =========================================================

    @Override
    public List<AppointmentHistoryRecord> findByAppointmentId(
            int appointmentId)
            throws SQLException {

        if (appointmentId <= 0) {

            throw new IllegalArgumentException(
                    "Valid appointment ID is required."
            );
        }


        String sql =
                """
                SELECT
                    h.history_id,
                    h.appointment_id,
                    h.appointment_number,
                    h.patient_id,

                    h.dentist_id,
                    d.dentist_code,
                    d.full_name AS dentist_name,

                    h.treatment_id,
                    t.treatment_code,
                    t.treatment_name,

                    h.appointment_date,
                    h.appointment_time,
                    h.status,
                    h.notes,

                    h.changed_by,
                    u.full_name AS changed_by_name,

                    h.change_type,
                    h.changed_at

                FROM appointment_history h

                LEFT JOIN dentists d
                    ON h.dentist_id = d.dentist_id

                LEFT JOIN treatments t
                    ON h.treatment_id = t.treatment_id

                LEFT JOIN users u
                    ON h.changed_by = u.user_id

                WHERE h.appointment_id = ?

                ORDER BY
                    h.changed_at DESC,
                    h.history_id DESC
                """;


        List<AppointmentHistoryRecord> history =
                new ArrayList<>();


        try (
                Connection connection =
                        connectionManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    appointmentId
            );


            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    history.add(
                            mapRow(resultSet)
                    );
                }
            }
        }


        return history;
    }


    // =========================================================
    // MAP HISTORY ROW
    // =========================================================

    private AppointmentHistoryRecord mapRow(
            ResultSet resultSet)
            throws SQLException {

        AppointmentHistoryRecord record =
                new AppointmentHistoryRecord();


        record.setHistoryId(
                resultSet.getInt(
                        "history_id"
                )
        );

        record.setAppointmentId(
                resultSet.getInt(
                        "appointment_id"
                )
        );

        record.setAppointmentNumber(
                resultSet.getString(
                        "appointment_number"
                )
        );

        record.setPatientId(
                resultSet.getInt(
                        "patient_id"
                )
        );


        record.setDentistId(
                resultSet.getInt(
                        "dentist_id"
                )
        );

        record.setDentistCode(
                resultSet.getString(
                        "dentist_code"
                )
        );

        record.setDentistName(
                resultSet.getString(
                        "dentist_name"
                )
        );


        record.setTreatmentId(
                resultSet.getInt(
                        "treatment_id"
                )
        );

        record.setTreatmentCode(
                resultSet.getString(
                        "treatment_code"
                )
        );

        record.setTreatmentName(
                resultSet.getString(
                        "treatment_name"
                )
        );


        Date date =
                resultSet.getDate(
                        "appointment_date"
                );

        if (date != null) {

            record.setAppointmentDate(
                    date.toLocalDate()
            );
        }


        Time time =
                resultSet.getTime(
                        "appointment_time"
                );

        if (time != null) {

            record.setAppointmentTime(
                    time.toLocalTime()
            );
        }


        record.setStatus(
                resultSet.getString(
                        "status"
                )
        );

        record.setNotes(
                resultSet.getString(
                        "notes"
                )
        );


        record.setChangedBy(
                resultSet.getInt(
                        "changed_by"
                )
        );

        record.setChangedByName(
                resultSet.getString(
                        "changed_by_name"
                )
        );

        record.setChangeType(
                resultSet.getString(
                        "change_type"
                )
        );


        Timestamp timestamp =
                resultSet.getTimestamp(
                        "changed_at"
                );

        if (timestamp != null) {

            record.setChangedAt(
                    timestamp.toLocalDateTime()
            );
        }


        return record;
    }
}
package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;
import com.sunrisedental.api.repository.AppointmentRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAppointmentRepository implements AppointmentRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcAppointmentRepository() {
        this.connectionManager =
                DatabaseConnectionManager.getInstance();
    }

    @Override
    public List<Appointment> findAll()
            throws SQLException {

        String sql = """
                SELECT
                    appointment_id,
                    appointment_number,
                    patient_id,
                    dentist_id,
                    treatment_id,
                    appointment_date,
                    appointment_time,
                    status,
                    notes,
                    created_by,
                    created_at,
                    updated_at
                FROM appointments
                ORDER BY appointment_date,
                         appointment_time
                """;

        List<Appointment> appointments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {

            while (resultSet.next()) {
                appointments.add(
                        mapRow(resultSet)
                );
            }
        }

        return appointments;
    }

    @Override
    public Optional<Appointment> findById(
            int appointmentId)
            throws SQLException {

        String sql = """
                SELECT
                    appointment_id,
                    appointment_number,
                    patient_id,
                    dentist_id,
                    treatment_id,
                    appointment_date,
                    appointment_time,
                    status,
                    notes,
                    created_by,
                    created_at,
                    updated_at
                FROM appointments
                WHERE appointment_id = ?
                """;

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

                if (resultSet.next()) {
                    return Optional.of(
                            mapRow(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Appointment> findByNumber(
            String appointmentNumber)
            throws SQLException {

        String sql = """
                SELECT
                    appointment_id,
                    appointment_number,
                    patient_id,
                    dentist_id,
                    treatment_id,
                    appointment_date,
                    appointment_time,
                    status,
                    notes,
                    created_by,
                    created_at,
                    updated_at
                FROM appointments
                WHERE appointment_number = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    appointmentNumber
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapRow(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Appointment> findByDate(
            LocalDate appointmentDate)
            throws SQLException {

        String sql = """
                SELECT
                    appointment_id,
                    appointment_number,
                    patient_id,
                    dentist_id,
                    treatment_id,
                    appointment_date,
                    appointment_time,
                    status,
                    notes,
                    created_by,
                    created_at,
                    updated_at
                FROM appointments
                WHERE appointment_date = ?
                ORDER BY appointment_time
                """;

        List<Appointment> appointments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(appointmentDate)
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                while (resultSet.next()) {
                    appointments.add(
                            mapRow(resultSet)
                    );
                }
            }
        }

        return appointments;
    }

    @Override
    public List<Appointment> findByPatientId(
            int patientId)
            throws SQLException {

        String sql = """
                SELECT
                    appointment_id,
                    appointment_number,
                    patient_id,
                    dentist_id,
                    treatment_id,
                    appointment_date,
                    appointment_time,
                    status,
                    notes,
                    created_by,
                    created_at,
                    updated_at
                FROM appointments
                WHERE patient_id = ?
                ORDER BY appointment_date DESC,
                         appointment_time DESC
                """;

        List<Appointment> appointments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    patientId
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                while (resultSet.next()) {
                    appointments.add(
                            mapRow(resultSet)
                    );
                }
            }
        }

        return appointments;
    }

    @Override
    public boolean existsDentistBooking(
            int dentistId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Integer excludeAppointmentId)
            throws SQLException {

        String sql;

        if (excludeAppointmentId == null) {

            sql = """
                    SELECT 1
                    FROM appointments
                    WHERE dentist_id = ?
                      AND appointment_date = ?
                      AND appointment_time = ?
                    LIMIT 1
                    """;

        } else {

            sql = """
                    SELECT 1
                    FROM appointments
                    WHERE dentist_id = ?
                      AND appointment_date = ?
                      AND appointment_time = ?
                      AND appointment_id <> ?
                    LIMIT 1
                    """;
        }

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    dentistId
            );

            statement.setDate(
                    2,
                    Date.valueOf(appointmentDate)
            );

            statement.setTime(
                    3,
                    Time.valueOf(appointmentTime)
            );

            if (excludeAppointmentId != null) {
                statement.setInt(
                        4,
                        excludeAppointmentId
                );
            }

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                return resultSet.next();
            }
        }
    }

    @Override
    public Appointment save(
            Appointment appointment)
            throws SQLException {

        String sql = """
                INSERT INTO appointments
                (
                    appointment_number,
                    patient_id,
                    dentist_id,
                    treatment_id,
                    appointment_date,
                    appointment_time,
                    status,
                    notes,
                    created_by
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    )
        ) {

            statement.setString(
                    1,
                    appointment.getAppointmentNumber()
            );

            statement.setInt(
                    2,
                    appointment.getPatientId()
            );

            statement.setInt(
                    3,
                    appointment.getDentistId()
            );

            statement.setInt(
                    4,
                    appointment.getTreatmentId()
            );

            statement.setDate(
                    5,
                    Date.valueOf(
                            appointment.getAppointmentDate()
                    )
            );

            statement.setTime(
                    6,
                    Time.valueOf(
                            appointment.getAppointmentTime()
                    )
            );

            statement.setString(
                    7,
                    appointment.getStatus()
                            .name()
                            .toLowerCase()
            );

            statement.setString(
                    8,
                    appointment.getNotes()
            );

            statement.setInt(
                    9,
                    appointment.getCreatedBy()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Creating appointment failed. No rows affected."
                );
            }

            try (
                ResultSet generatedKeys =
                        statement.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {
                    appointment.setAppointmentId(
                            generatedKeys.getInt(1)
                    );
                }
            }
        }

        return appointment;
    }

    @Override
    public boolean update(
            Appointment appointment)
            throws SQLException {

        String sql = """
                UPDATE appointments
                SET patient_id = ?,
                    dentist_id = ?,
                    treatment_id = ?,
                    appointment_date = ?,
                    appointment_time = ?,
                    status = ?,
                    notes = ?
                WHERE appointment_id = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    appointment.getPatientId()
            );

            statement.setInt(
                    2,
                    appointment.getDentistId()
            );

            statement.setInt(
                    3,
                    appointment.getTreatmentId()
            );

            statement.setDate(
                    4,
                    Date.valueOf(
                            appointment.getAppointmentDate()
                    )
            );

            statement.setTime(
                    5,
                    Time.valueOf(
                            appointment.getAppointmentTime()
                    )
            );

            statement.setString(
                    6,
                    appointment.getStatus()
                            .name()
                            .toLowerCase()
            );

            statement.setString(
                    7,
                    appointment.getNotes()
            );

            statement.setInt(
                    8,
                    appointment.getAppointmentId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    private Appointment mapRow(
            ResultSet resultSet)
            throws SQLException {

        Appointment appointment =
                new Appointment();

        appointment.setAppointmentId(
                resultSet.getInt(
                        "appointment_id"
                )
        );

        appointment.setAppointmentNumber(
                resultSet.getString(
                        "appointment_number"
                )
        );

        appointment.setPatientId(
                resultSet.getInt(
                        "patient_id"
                )
        );

        appointment.setDentistId(
                resultSet.getInt(
                        "dentist_id"
                )
        );

        appointment.setTreatmentId(
                resultSet.getInt(
                        "treatment_id"
                )
        );

        appointment.setAppointmentDate(
                resultSet.getDate(
                        "appointment_date"
                ).toLocalDate()
        );

        appointment.setAppointmentTime(
                resultSet.getTime(
                        "appointment_time"
                ).toLocalTime()
        );

        appointment.setStatus(
                AppointmentStatus.fromString(
                        resultSet.getString(
                                "status"
                        )
                )
        );

        appointment.setNotes(
                resultSet.getString(
                        "notes"
                )
        );

        appointment.setCreatedBy(
                resultSet.getInt(
                        "created_by"
                )
        );

        if (resultSet.getTimestamp("created_at")
                != null) {

            appointment.setCreatedAt(
                    resultSet
                            .getTimestamp(
                                    "created_at"
                            )
                            .toLocalDateTime()
            );
        }

        if (resultSet.getTimestamp("updated_at")
                != null) {

            appointment.setUpdatedAt(
                    resultSet
                            .getTimestamp(
                                    "updated_at"
                            )
                            .toLocalDateTime()
            );
        }

        return appointment;
    }
}
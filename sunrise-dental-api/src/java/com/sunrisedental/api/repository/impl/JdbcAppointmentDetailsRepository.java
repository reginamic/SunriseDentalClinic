package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.AppointmentDetails;
import com.sunrisedental.api.repository.AppointmentDetailsRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of AppointmentDetailsRepository.
 *
 * Reads enriched appointment information from the
 * vw_appointment_details database View.
 *
 * This repository is read-only.
 */
public class JdbcAppointmentDetailsRepository
        implements AppointmentDetailsRepository {

    private final DatabaseConnectionManager
            connectionManager;

    public JdbcAppointmentDetailsRepository() {

        this.connectionManager =
                DatabaseConnectionManager
                        .getInstance();
    }

    /*
     * =========================================================
     * FIND ALL
     * =========================================================
     */

    @Override
    public List<AppointmentDetails> findAll()
            throws SQLException {

        String sql =
                """
                SELECT *
                FROM vw_appointment_details
                ORDER BY appointment_date,
                         appointment_time,
                         dentist_name
                """;

        List<AppointmentDetails> appointments =
                new ArrayList<>();

        try (
                Connection connection =
                        connectionManager
                                .getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        );

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                appointments.add(
                        mapRow(
                                resultSet
                        )
                );
            }
        }

        return appointments;
    }

    /*
     * =========================================================
     * FIND BY ID
     * =========================================================
     */

    @Override
    public Optional<AppointmentDetails> findById(
            int appointmentId)
            throws SQLException {

        String sql =
                """
                SELECT *
                FROM vw_appointment_details
                WHERE appointment_id = ?
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
                            mapRow(
                                    resultSet
                            )
                    );
                }
            }
        }

        return Optional.empty();
    }

    /*
     * =========================================================
     * FIND BY APPOINTMENT NUMBER
     * =========================================================
     */

    @Override
    public Optional<AppointmentDetails>
            findByAppointmentNumber(
                    String appointmentNumber)
            throws SQLException {

        String sql =
                """
                SELECT *
                FROM vw_appointment_details
                WHERE appointment_number = ?
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
                            mapRow(
                                    resultSet
                            )
                    );
                }
            }
        }

        return Optional.empty();
    }

    /*
     * =========================================================
     * FIND BY DATE
     * =========================================================
     */

    @Override
    public List<AppointmentDetails> findByDate(
            LocalDate appointmentDate)
            throws SQLException {

        String sql =
                """
                SELECT *
                FROM vw_appointment_details
                WHERE appointment_date = ?
                ORDER BY appointment_time,
                         dentist_name
                """;

        List<AppointmentDetails> appointments =
                new ArrayList<>();

        try (
                Connection connection =
                        connectionManager
                                .getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        )
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(
                            appointmentDate
                    )
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    appointments.add(
                            mapRow(
                                    resultSet
                            )
                    );
                }
            }
        }

        return appointments;
    }

    /*
     * =========================================================
     * FIND BY PATIENT
     * =========================================================
     */

    @Override
    public List<AppointmentDetails> findByPatientId(
            int patientId)
            throws SQLException {

        String sql =
                """
                SELECT *
                FROM vw_appointment_details
                WHERE patient_id = ?
                ORDER BY appointment_date DESC,
                         appointment_time DESC
                """;

        List<AppointmentDetails> appointments =
                new ArrayList<>();

        try (
                Connection connection =
                        connectionManager
                                .getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        )
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
                            mapRow(
                                    resultSet
                            )
                    );
                }
            }
        }

        return appointments;
    }

    /*
     * =========================================================
     * RESULTSET MAPPING
     * =========================================================
     */

    private AppointmentDetails mapRow(
            ResultSet resultSet)
            throws SQLException {

        AppointmentDetails details =
                new AppointmentDetails();

        /*
         * Appointment
         */
        details.setAppointmentId(
                resultSet.getInt(
                        "appointment_id"
                )
        );

        details.setAppointmentNumber(
                resultSet.getString(
                        "appointment_number"
                )
        );

        Date appointmentDate =
                resultSet.getDate(
                        "appointment_date"
                );

        if (appointmentDate != null) {

            details.setAppointmentDate(
                    appointmentDate
                            .toLocalDate()
            );
        }

        Time appointmentTime =
                resultSet.getTime(
                        "appointment_time"
                );

        if (appointmentTime != null) {

            details.setAppointmentTime(
                    appointmentTime
                            .toLocalTime()
            );
        }

        details.setStatus(
                resultSet.getString(
                        "status"
                )
        );

        details.setNotes(
                resultSet.getString(
                        "notes"
                )
        );

        /*
         * Patient
         */
        details.setPatientId(
                resultSet.getInt(
                        "patient_id"
                )
        );

        details.setPatientCode(
                resultSet.getString(
                        "patient_code"
                )
        );

        details.setPatientName(
                resultSet.getString(
                        "patient_name"
                )
        );

        details.setPatientAddress(
                resultSet.getString(
                        "patient_address"
                )
        );

        details.setPatientContactNumber(
                resultSet.getString(
                        "patient_contact_number"
                )
        );

        details.setPatientEmail(
                resultSet.getString(
                        "patient_email"
                )
        );

        /*
         * Dentist
         */
        details.setDentistId(
                resultSet.getInt(
                        "dentist_id"
                )
        );

        details.setDentistCode(
                resultSet.getString(
                        "dentist_code"
                )
        );

        details.setDentistName(
                resultSet.getString(
                        "dentist_name"
                )
        );

        details.setDentistSpecialization(
                resultSet.getString(
                        "dentist_specialization"
                )
        );

        details.setDentistContactNumber(
                resultSet.getString(
                        "dentist_contact_number"
                )
        );

        details.setDentistEmail(
                resultSet.getString(
                        "dentist_email"
                )
        );

        details.setDentistActive(
                resultSet.getBoolean(
                        "dentist_active"
                )
        );

        /*
         * Treatment
         */
        details.setTreatmentId(
                resultSet.getInt(
                        "treatment_id"
                )
        );

        details.setTreatmentCode(
                resultSet.getString(
                        "treatment_code"
                )
        );

        details.setTreatmentName(
                resultSet.getString(
                        "treatment_name"
                )
        );

        details.setTreatmentDescription(
                resultSet.getString(
                        "treatment_description"
                )
        );

        details.setTreatmentPrice(
                resultSet.getBigDecimal(
                        "treatment_price"
                )
        );

        details.setConsultationFee(
                resultSet.getBigDecimal(
                        "consultation_fee"
                )
        );

        int duration =
                resultSet.getInt(
                        "estimated_duration_minutes"
                );

        if (resultSet.wasNull()) {

            details.setEstimatedDurationMinutes(
                    null
            );

        } else {

            details.setEstimatedDurationMinutes(
                    duration
            );
        }

        details.setTreatmentActive(
                resultSet.getBoolean(
                        "treatment_active"
                )
        );

        /*
         * Calculated fields from database View
         */
        Time estimatedEndTime =
                resultSet.getTime(
                        "estimated_end_time"
                );

        if (estimatedEndTime != null) {

            details.setEstimatedEndTime(
                    estimatedEndTime
                            .toLocalTime()
            );
        }

        details.setEstimatedTotalCost(
                resultSet.getBigDecimal(
                        "estimated_total_cost"
                )
        );

        /*
         * Audit
         */
        details.setCreatedBy(
                resultSet.getInt(
                        "created_by"
                )
        );

        return details;
    }
}
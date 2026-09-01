package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.AppointmentDetails;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Read-only repository for enriched appointment information.
 *
 * Data is retrieved from the vw_appointment_details
 * database View.
 *
 * This repository is intentionally separated from
 * AppointmentRepository because AppointmentRepository
 * manages appointment persistence and scheduling logic,
 * while this repository supports detailed display,
 * searching and reporting.
 */
public interface AppointmentDetailsRepository {

    /**
     * Returns all appointments with complete patient,
     * dentist and treatment information.
     */
    List<AppointmentDetails> findAll()
            throws SQLException;

    /**
     * Returns complete details for one appointment
     * using its internal database ID.
     */
    Optional<AppointmentDetails> findById(
            int appointmentId)
            throws SQLException;

    /**
     * Returns complete details for one appointment
     * using its unique appointment number.
     */
    Optional<AppointmentDetails> findByAppointmentNumber(
            String appointmentNumber)
            throws SQLException;

    /**
     * Returns enriched appointments for a selected date.
     */
    List<AppointmentDetails> findByDate(
            LocalDate appointmentDate)
            throws SQLException;

    /**
     * Returns enriched appointment history/list
     * for a selected patient.
     */
    List<AppointmentDetails> findByPatientId(
            int patientId)
            throws SQLException;
}
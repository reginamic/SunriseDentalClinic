package com.sunrisedental.api.service;

import com.sunrisedental.api.model.AppointmentHistoryRecord;

import com.sunrisedental.api.repository.AppointmentHistoryRepository;

import com.sunrisedental.api.repository.impl.JdbcAppointmentHistoryRepository;

import java.sql.SQLException;

import java.util.List;
import java.util.Objects;


/**
 * Business service for appointment audit/history retrieval.
 *
 * Appointment history records represent previous appointment
 * states captured through the Memento design pattern.
 */
public class AppointmentHistoryService {

    private final AppointmentHistoryRepository
            appointmentHistoryRepository;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    /**
     * Used by the real application.
     */
    public AppointmentHistoryService() {

        this(
                new JdbcAppointmentHistoryRepository()
        );
    }


    // =========================================================
    // DEPENDENCY-INJECTION CONSTRUCTOR
    // =========================================================

    /**
     * Allows the repository to be replaced with a mock/fake
     * during automated testing.
     */
    public AppointmentHistoryService(
            AppointmentHistoryRepository
                    appointmentHistoryRepository) {

        this.appointmentHistoryRepository =
                Objects.requireNonNull(
                        appointmentHistoryRepository,
                        "AppointmentHistoryRepository cannot be null."
                );
    }


    // =========================================================
    // GET APPOINTMENT HISTORY
    // =========================================================

    /**
     * Returns all previous states belonging to one appointment.
     *
     * Latest history entry is returned first.
     */
    public List<AppointmentHistoryRecord>
            getAppointmentHistory(
                    int appointmentId)
            throws SQLException {

        if (appointmentId <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID must be greater than zero."
            );
        }


        return appointmentHistoryRepository
                .findByAppointmentId(
                        appointmentId
                );
    }
}
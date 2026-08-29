package com.sunrisedental.api.repository;

import com.sunrisedental.api.pattern.memento.AppointmentMemento;

import java.sql.SQLException;

public interface AppointmentHistoryRepository {

    /**
     * Persists the previous state of an appointment.
     *
     * @param memento immutable snapshot captured before modification
     * @param changedBy user who performed the change
     * @param changeType UPDATE or CANCEL
     */
    void save(
            AppointmentMemento memento,
            int changedBy,
            String changeType)
            throws SQLException;
}
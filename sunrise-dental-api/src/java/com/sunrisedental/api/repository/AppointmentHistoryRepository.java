package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.AppointmentHistoryRecord;
import com.sunrisedental.api.pattern.memento.AppointmentMemento;

import java.sql.SQLException;
import java.util.List;

/**
 * Repository responsible for storing and retrieving
 * historical appointment states.
 *
 * Historical appointment states are created using
 * the Memento design pattern.
 */
public interface AppointmentHistoryRepository {

    /**
     * Saves the previous state of an appointment.
     *
     * @param memento immutable appointment snapshot
     * @param changedBy user who performed the change
     * @param changeType UPDATE or CANCEL
     */
    void save(
            AppointmentMemento memento,
            int changedBy,
            String changeType)
            throws SQLException;


    /**
     * Retrieves all historical states belonging
     * to one appointment.
     *
     * @param appointmentId appointment database ID
     * @return appointment history records
     */
    List<AppointmentHistoryRecord> findByAppointmentId(
            int appointmentId)
            throws SQLException;
}
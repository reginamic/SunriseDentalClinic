package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.Appointment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

    List<Appointment> findAll() throws SQLException;

    Optional<Appointment> findById(int appointmentId)
            throws SQLException;

    Optional<Appointment> findByNumber(String appointmentNumber)
            throws SQLException;

    List<Appointment> findByDate(LocalDate appointmentDate)
            throws SQLException;

    List<Appointment> findByPatientId(int patientId)
            throws SQLException;

  boolean existsDentistBooking(
        int dentistId,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        int proposedDurationMinutes,
        Integer excludeAppointmentId)
        throws SQLException;

    Appointment save(Appointment appointment)
            throws SQLException;

    boolean update(Appointment appointment)
            throws SQLException;
}
package com.sunrisedental.api.pattern.memento;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Memento object that stores an immutable snapshot of an
 * Appointment before it is modified.
 *
 * The snapshot is used by the appointment history mechanism
 * when an appointment is rescheduled, edited or cancelled.
 *
 * This class intentionally contains no behaviour that changes
 * appointment state. Once created, the snapshot cannot change.
 */
public final class AppointmentMemento {

    private final int appointmentId;
    private final String appointmentNumber;

    private final int patientId;
    private final int dentistId;
    private final int treatmentId;

    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;

    private final AppointmentStatus status;
    private final String notes;

    /**
     * Creates an immutable snapshot from an Appointment.
     *
     * @param appointment appointment whose current state
     *                    should be preserved
     */
    public AppointmentMemento(
            Appointment appointment) {

        Objects.requireNonNull(
                appointment,
                "Appointment cannot be null."
        );

        this.appointmentId =
                appointment.getAppointmentId();

        this.appointmentNumber =
                appointment.getAppointmentNumber();

        this.patientId =
                appointment.getPatientId();

        this.dentistId =
                appointment.getDentistId();

        this.treatmentId =
                appointment.getTreatmentId();

        this.appointmentDate =
                appointment.getAppointmentDate();

        this.appointmentTime =
                appointment.getAppointmentTime();

        this.status =
                appointment.getStatus();

        this.notes =
                appointment.getNotes();
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getDentistId() {
        return dentistId;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }
}
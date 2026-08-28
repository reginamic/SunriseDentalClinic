package com.sunrisedental.api.pattern.builder;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentBuilder {

    private String appointmentNumber;
    private int patientId;
    private int dentistId;
    private int treatmentId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;
    private String notes;
    private int createdBy;

    public AppointmentBuilder appointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
        return this;
    }

    public AppointmentBuilder patientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public AppointmentBuilder dentistId(int dentistId) {
        this.dentistId = dentistId;
        return this;
    }

    public AppointmentBuilder treatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
        return this;
    }

    public AppointmentBuilder appointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
        return this;
    }

    public AppointmentBuilder appointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        return this;
    }

    public AppointmentBuilder status(AppointmentStatus status) {

        if (status != null) {
            this.status = status;
        }

        return this;
    }

    public AppointmentBuilder notes(String notes) {
        this.notes = notes;
        return this;
    }

    public AppointmentBuilder createdBy(int createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Appointment build() {

        Appointment appointment = new Appointment();

        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setPatientId(patientId);
        appointment.setDentistId(dentistId);
        appointment.setTreatmentId(treatmentId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(status);
        appointment.setNotes(notes);
        appointment.setCreatedBy(createdBy);

        return appointment;
    }
}
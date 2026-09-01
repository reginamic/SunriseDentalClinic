package com.sunrisedental.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Read model representing one historical appointment state.
 *
 * Historical states are created from Appointment Mementos
 * and stored in appointment_history.
 */
public class AppointmentHistoryRecord {

    private int historyId;

    private int appointmentId;

    private String appointmentNumber;

    private int patientId;

    private int dentistId;

    private String dentistCode;

    private String dentistName;

    private int treatmentId;

    private String treatmentCode;

    private String treatmentName;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String status;

    private String notes;

    private int changedBy;

    private String changedByName;

    private String changeType;

    private LocalDateTime changedAt;


    public AppointmentHistoryRecord() {
    }


    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }


    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }


    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(
            String appointmentNumber) {

        this.appointmentNumber =
                appointmentNumber;
    }


    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }


    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }


    public String getDentistCode() {
        return dentistCode;
    }

    public void setDentistCode(String dentistCode) {
        this.dentistCode = dentistCode;
    }


    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }


    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }


    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(
            String treatmentCode) {

        this.treatmentCode =
                treatmentCode;
    }


    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(
            String treatmentName) {

        this.treatmentName =
                treatmentName;
    }


    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(
            LocalDate appointmentDate) {

        this.appointmentDate =
                appointmentDate;
    }


    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(
            LocalTime appointmentTime) {

        this.appointmentTime =
                appointmentTime;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public int getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(int changedBy) {
        this.changedBy = changedBy;
    }


    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(
            String changedByName) {

        this.changedByName =
                changedByName;
    }


    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(
            String changeType) {

        this.changeType =
                changeType;
    }


    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(
            LocalDateTime changedAt) {

        this.changedAt =
                changedAt;
    }
}
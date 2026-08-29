package com.sunrisedental.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Read-only model containing enriched appointment information.
 *
 * Data is loaded from the vw_appointment_details database View.
 * It combines appointment, patient, dentist and treatment
 * information for Web display and reporting.
 */
public class AppointmentDetails {

    // =========================================================
    // APPOINTMENT
    // =========================================================

    private int appointmentId;

    private String appointmentNumber;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String status;

    private String notes;

    // =========================================================
    // PATIENT
    // =========================================================

    private int patientId;

    private String patientCode;

    private String patientName;

    private String patientAddress;

    private String patientContactNumber;

    private String patientEmail;

    // =========================================================
    // DENTIST
    // =========================================================

    private int dentistId;

    private String dentistCode;

    private String dentistName;

    private String dentistSpecialization;

    private String dentistContactNumber;

    private String dentistEmail;

    private boolean dentistActive;

    // =========================================================
    // TREATMENT
    // =========================================================

    private int treatmentId;

    private String treatmentCode;

    private String treatmentName;

    private String treatmentDescription;

    private BigDecimal treatmentPrice;

    private BigDecimal consultationFee;

    private Integer estimatedDurationMinutes;

    private boolean treatmentActive;

    // =========================================================
    // CALCULATED VIEW FIELDS
    // =========================================================

    private LocalTime estimatedEndTime;

    private BigDecimal estimatedTotalCost;

    // =========================================================
    // AUDIT
    // =========================================================

    private int createdBy;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AppointmentDetails() {
    }

    // =========================================================
    // APPOINTMENT GETTERS / SETTERS
    // =========================================================

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
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

    // =========================================================
    // PATIENT GETTERS / SETTERS
    // =========================================================

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getPatientContactNumber() {
        return patientContactNumber;
    }

    public void setPatientContactNumber(String patientContactNumber) {
        this.patientContactNumber = patientContactNumber;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    // =========================================================
    // DENTIST GETTERS / SETTERS
    // =========================================================

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

    public String getDentistSpecialization() {
        return dentistSpecialization;
    }

    public void setDentistSpecialization(
            String dentistSpecialization) {

        this.dentistSpecialization =
                dentistSpecialization;
    }

    public String getDentistContactNumber() {
        return dentistContactNumber;
    }

    public void setDentistContactNumber(
            String dentistContactNumber) {

        this.dentistContactNumber =
                dentistContactNumber;
    }

    public String getDentistEmail() {
        return dentistEmail;
    }

    public void setDentistEmail(String dentistEmail) {
        this.dentistEmail = dentistEmail;
    }

    public boolean isDentistActive() {
        return dentistActive;
    }

    public void setDentistActive(boolean dentistActive) {
        this.dentistActive = dentistActive;
    }

    // =========================================================
    // TREATMENT GETTERS / SETTERS
    // =========================================================

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getTreatmentDescription() {
        return treatmentDescription;
    }

    public void setTreatmentDescription(
            String treatmentDescription) {

        this.treatmentDescription =
                treatmentDescription;
    }

    public BigDecimal getTreatmentPrice() {
        return treatmentPrice;
    }

    public void setTreatmentPrice(
            BigDecimal treatmentPrice) {

        this.treatmentPrice =
                treatmentPrice;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(
            BigDecimal consultationFee) {

        this.consultationFee =
                consultationFee;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(
            Integer estimatedDurationMinutes) {

        this.estimatedDurationMinutes =
                estimatedDurationMinutes;
    }

    public boolean isTreatmentActive() {
        return treatmentActive;
    }

    public void setTreatmentActive(
            boolean treatmentActive) {

        this.treatmentActive =
                treatmentActive;
    }

    // =========================================================
    // CALCULATED GETTERS / SETTERS
    // =========================================================

    public LocalTime getEstimatedEndTime() {
        return estimatedEndTime;
    }

    public void setEstimatedEndTime(
            LocalTime estimatedEndTime) {

        this.estimatedEndTime =
                estimatedEndTime;
    }

    public BigDecimal getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public void setEstimatedTotalCost(
            BigDecimal estimatedTotalCost) {

        this.estimatedTotalCost =
                estimatedTotalCost;
    }

    // =========================================================
    // AUDIT GETTERS / SETTERS
    // =========================================================

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}
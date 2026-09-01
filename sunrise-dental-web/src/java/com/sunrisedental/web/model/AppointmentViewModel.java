package com.sunrisedental.web.model;

/**
 * View model used by the Sunrise Dental Web application
 * for appointment information received from the REST API.
 *
 * The Web tier does not communicate directly with MySQL.
 * Appointment data is received from the API as JSON and
 * converted into this view model.
 */
public class AppointmentViewModel {

    private int appointmentId;

    private String appointmentNumber;

    private int patientId;

    private int dentistId;

    private int treatmentId;

    private String appointmentDate;

    private String appointmentTime;

    private String status;

    private String notes;

    private int createdBy;

    /*
     * Additional display fields.
     *
     * These fields will later allow the Web application
     * to display meaningful names instead of only IDs.
     */
    private String patientCode;

    private String patientName;

    private String patientAddress;

    private String patientContactNumber;

    private String patientEmail;

    private String dentistCode;

    private String dentistName;

    private String dentistSpecialization;

    private String treatmentCode;

    private String treatmentName;

    private Double treatmentPrice;

    private Double consultationFee;

    private Integer estimatedDurationMinutes;

    private String estimatedEndTime;

    private Double estimatedTotalCost;

    /*
     * =========================================================
     * CONSTRUCTORS
     * =========================================================
     */

    public AppointmentViewModel() {
    }

    /*
     * =========================================================
     * APPOINTMENT GETTERS / SETTERS
     * =========================================================
     */

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

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    /*
     * =========================================================
     * PATIENT DISPLAY INFORMATION
     * =========================================================
     */

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

    public void setPatientContactNumber(
            String patientContactNumber) {

        this.patientContactNumber =
                patientContactNumber;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    /*
     * =========================================================
     * DENTIST DISPLAY INFORMATION
     * =========================================================
     */

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

    /*
     * =========================================================
     * TREATMENT DISPLAY INFORMATION
     * =========================================================
     */

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

    public Double getTreatmentPrice() {
        return treatmentPrice;
    }

    public void setTreatmentPrice(Double treatmentPrice) {
        this.treatmentPrice = treatmentPrice;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(
            Integer estimatedDurationMinutes) {

        this.estimatedDurationMinutes =
                estimatedDurationMinutes;
    }

    public String getEstimatedEndTime() {
        return estimatedEndTime;
    }

    public void setEstimatedEndTime(
            String estimatedEndTime) {

        this.estimatedEndTime =
                estimatedEndTime;
    }

    public Double getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public void setEstimatedTotalCost(
            Double estimatedTotalCost) {

        this.estimatedTotalCost =
                estimatedTotalCost;
    }

    /*
     * =========================================================
     * VIEW HELPERS
     * =========================================================
     */

    public boolean isScheduled() {

        return status != null
                && status.equalsIgnoreCase(
                        "SCHEDULED"
                );
    }

    public boolean isCompleted() {

        return status != null
                && status.equalsIgnoreCase(
                        "COMPLETED"
                );
    }

    public boolean isCancelled() {

        return status != null
                && status.equalsIgnoreCase(
                        "CANCELLED"
                );
    }
}
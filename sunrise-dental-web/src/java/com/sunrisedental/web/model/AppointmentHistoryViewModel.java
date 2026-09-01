package com.sunrisedental.web.model;

/**
 * Web view model representing one historical
 * appointment state preserved by the Memento pattern.
 */
public class AppointmentHistoryViewModel {

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

    private String appointmentDate;

    private String appointmentTime;

    private String status;

    private String notes;

    private int changedBy;

    private String changedByName;

    private String changeType;

    private String changedAt;


    public AppointmentHistoryViewModel() {
    }


    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(
            int historyId) {

        this.historyId =
                historyId;
    }


    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(
            int appointmentId) {

        this.appointmentId =
                appointmentId;
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

    public void setPatientId(
            int patientId) {

        this.patientId =
                patientId;
    }


    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(
            int dentistId) {

        this.dentistId =
                dentistId;
    }


    public String getDentistCode() {
        return dentistCode;
    }

    public void setDentistCode(
            String dentistCode) {

        this.dentistCode =
                dentistCode;
    }


    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(
            String dentistName) {

        this.dentistName =
                dentistName;
    }


    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(
            int treatmentId) {

        this.treatmentId =
                treatmentId;
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


    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(
            String appointmentDate) {

        this.appointmentDate =
                appointmentDate;
    }


    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(
            String appointmentTime) {

        this.appointmentTime =
                appointmentTime;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {

        this.status =
                status;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes =
                notes;
    }


    public int getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(
            int changedBy) {

        this.changedBy =
                changedBy;
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


    public String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(
            String changedAt) {

        this.changedAt =
                changedAt;
    }


    // =========================================================
    // DISPLAY HELPERS
    // =========================================================

    public boolean isUpdate() {

        return changeType != null
                && changeType.equalsIgnoreCase(
                        "UPDATE"
                );
    }


    public boolean isCancellation() {

        return changeType != null
                && changeType.equalsIgnoreCase(
                        "CANCEL"
                );
    }


    public String getDisplayChangeType() {

        if (isCancellation()) {
            return "Cancelled";
        }

        if (isUpdate()) {
            return "Updated / Rescheduled";
        }

        if (changeType == null
                || changeType.isBlank()) {

            return "Change";
        }

        return changeType;
    }


    public String getDisplayChangedBy() {

        if (changedByName != null
                && !changedByName.isBlank()) {

            return changedByName;
        }

        if (changedBy > 0) {

            return "User #" + changedBy;
        }

        return "Unknown User";
    }


    public String getDisplayDentist() {

        if (dentistName == null
                || dentistName.isBlank()) {

            return dentistCode == null
                    ? "—"
                    : dentistCode;
        }

        if (dentistCode == null
                || dentistCode.isBlank()) {

            return dentistName;
        }

        return dentistCode
                + " — "
                + dentistName;
    }


    public String getDisplayTreatment() {

        if (treatmentName == null
                || treatmentName.isBlank()) {

            return treatmentCode == null
                    ? "—"
                    : treatmentCode;
        }

        if (treatmentCode == null
                || treatmentCode.isBlank()) {

            return treatmentName;
        }

        return treatmentCode
                + " — "
                + treatmentName;
    }
}
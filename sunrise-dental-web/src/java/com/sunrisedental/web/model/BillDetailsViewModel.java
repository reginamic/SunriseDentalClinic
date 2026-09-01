package com.sunrisedental.web.model;

import java.math.BigDecimal;
import java.util.List;


public class BillDetailsViewModel {

    // =========================================================
    // BILL
    // =========================================================

    private int billId;

    private String billNumber;

    private BigDecimal subtotal;

    private BigDecimal additionalCharges;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private String paymentStatus;

    private int generatedBy;

    private String generatedAt;


    // =========================================================
    // APPOINTMENT
    // =========================================================

    private int appointmentId;

    private String appointmentNumber;

    private String appointmentDate;

    private String appointmentTime;

    private String appointmentStatus;


    // =========================================================
    // PATIENT
    // =========================================================

    private String patientCode;

    private String patientName;

    private String patientAddress;

    private String patientContactNumber;

    private String patientEmail;


    // =========================================================
    // DENTIST
    // =========================================================

    private String dentistCode;

    private String dentistName;

    private String dentistSpecialization;


    // =========================================================
    // TREATMENT
    // =========================================================

    private String treatmentCode;

    private String treatmentName;


    // =========================================================
    // ITEMS
    // =========================================================

    private List<BillViewModel.BillItemViewModel> items;


    public BillDetailsViewModel() {
    }


    // =========================================================
    // BILL GETTERS / SETTERS
    // =========================================================

    public int getBillId() {
        return billId;
    }


    public void setBillId(int billId) {
        this.billId = billId;
    }


    public String getBillNumber() {
        return billNumber;
    }


    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }


    public BigDecimal getSubtotal() {
        return subtotal;
    }


    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }


    public BigDecimal getAdditionalCharges() {
        return additionalCharges;
    }


    public void setAdditionalCharges(
            BigDecimal additionalCharges) {

        this.additionalCharges =
                additionalCharges;
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }


    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                discountAmount;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }


    public void setPaymentStatus(
            String paymentStatus) {

        this.paymentStatus =
                paymentStatus;
    }


    public int getGeneratedBy() {
        return generatedBy;
    }


    public void setGeneratedBy(
            int generatedBy) {

        this.generatedBy =
                generatedBy;
    }


    public String getGeneratedAt() {
        return generatedAt;
    }


    public void setGeneratedAt(
            String generatedAt) {

        this.generatedAt =
                generatedAt;
    }


    // =========================================================
    // APPOINTMENT GETTERS / SETTERS
    // =========================================================

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


    public String getAppointmentStatus() {
        return appointmentStatus;
    }


    public void setAppointmentStatus(
            String appointmentStatus) {

        this.appointmentStatus =
                appointmentStatus;
    }


    // =========================================================
    // PATIENT GETTERS / SETTERS
    // =========================================================

    public String getPatientCode() {
        return patientCode;
    }


    public void setPatientCode(
            String patientCode) {

        this.patientCode =
                patientCode;
    }


    public String getPatientName() {
        return patientName;
    }


    public void setPatientName(
            String patientName) {

        this.patientName =
                patientName;
    }


    public String getPatientAddress() {
        return patientAddress;
    }


    public void setPatientAddress(
            String patientAddress) {

        this.patientAddress =
                patientAddress;
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


    public void setPatientEmail(
            String patientEmail) {

        this.patientEmail =
                patientEmail;
    }


    // =========================================================
    // DENTIST GETTERS / SETTERS
    // =========================================================

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


    public String getDentistSpecialization() {
        return dentistSpecialization;
    }


    public void setDentistSpecialization(
            String dentistSpecialization) {

        this.dentistSpecialization =
                dentistSpecialization;
    }


    // =========================================================
    // TREATMENT GETTERS / SETTERS
    // =========================================================

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


    // =========================================================
    // ITEMS
    // =========================================================

    public List<BillViewModel.BillItemViewModel> getItems() {
        return items;
    }


    public void setItems(
            List<BillViewModel.BillItemViewModel> items) {

        this.items = items;
    }


    // =========================================================
    // DISPLAY HELPERS
    // =========================================================

    public boolean isPaid() {

        return paymentStatus != null
                && paymentStatus.equalsIgnoreCase(
                        "PAID"
                );
    }


    public boolean isUnpaid() {

        return paymentStatus != null
                && paymentStatus.equalsIgnoreCase(
                        "UNPAID"
                );
    }


    public boolean isCompletedAppointment() {

        return appointmentStatus != null
                && appointmentStatus.equalsIgnoreCase(
                        "COMPLETED"
                );
    }
}
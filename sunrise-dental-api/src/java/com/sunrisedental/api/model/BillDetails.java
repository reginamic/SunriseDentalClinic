package com.sunrisedental.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Read-only DTO containing the complete information required
 * for billing display and printable patient receipts.
 *
 * Financial values originate from the persisted Bill record,
 * while patient, dentist and appointment information is
 * obtained through AppointmentDetails.
 */
public class BillDetails {

    // =========================================================
    // BILL
    // =========================================================

    private int billId;

    private String billNumber;

    private BigDecimal subtotal;

    private BigDecimal additionalCharges;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private BillStatus paymentStatus;

    private int generatedBy;

    private LocalDateTime generatedAt;

    private final List<BillItem> items;


    // =========================================================
    // APPOINTMENT
    // =========================================================

    private int appointmentId;

    private String appointmentNumber;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

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
    // CONSTRUCTOR
    // =========================================================

    public BillDetails() {

        this.subtotal =
                BigDecimal.ZERO;

        this.additionalCharges =
                BigDecimal.ZERO;

        this.discountAmount =
                BigDecimal.ZERO;

        this.totalAmount =
                BigDecimal.ZERO;

        this.items =
                new ArrayList<>();
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

        this.subtotal =
                safeAmount(
                        subtotal
                );
    }


    public BigDecimal getAdditionalCharges() {
        return additionalCharges;
    }


    public void setAdditionalCharges(
            BigDecimal additionalCharges) {

        this.additionalCharges =
                safeAmount(
                        additionalCharges
                );
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }


    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                safeAmount(
                        discountAmount
                );
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                safeAmount(
                        totalAmount
                );
    }


    public BillStatus getPaymentStatus() {
        return paymentStatus;
    }


    public void setPaymentStatus(
            BillStatus paymentStatus) {

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


    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }


    public void setGeneratedAt(
            LocalDateTime generatedAt) {

        this.generatedAt =
                generatedAt;
    }


    public List<BillItem> getItems() {

        return Collections.unmodifiableList(
                items
        );
    }


    public void addItem(
            BillItem item) {

        if (item != null) {

            items.add(
                    item
            );
        }
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
    // MONEY HELPER
    // =========================================================

    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }
}
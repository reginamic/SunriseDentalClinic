package com.sunrisedental.web.model;

import java.math.BigDecimal;

public class DashboardSummaryViewModel {

    private int totalPatients;

    private int totalDentists;
    private int activeDentists;

    private int totalTreatments;
    private int activeTreatments;

    private int totalAppointments;
    private int scheduledAppointments;
    private int completedAppointments;
    private int cancelledAppointments;

    private int totalBills;
    private int paidBills;
    private int unpaidBills;

    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal outstandingAmount;

    public DashboardSummaryViewModel() {

        this.totalBilled =
                BigDecimal.ZERO;

        this.totalPaid =
                BigDecimal.ZERO;

        this.outstandingAmount =
                BigDecimal.ZERO;
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(
            int totalPatients) {

        this.totalPatients =
                totalPatients;
    }

    public int getTotalDentists() {
        return totalDentists;
    }

    public void setTotalDentists(
            int totalDentists) {

        this.totalDentists =
                totalDentists;
    }

    public int getActiveDentists() {
        return activeDentists;
    }

    public void setActiveDentists(
            int activeDentists) {

        this.activeDentists =
                activeDentists;
    }

    public int getTotalTreatments() {
        return totalTreatments;
    }

    public void setTotalTreatments(
            int totalTreatments) {

        this.totalTreatments =
                totalTreatments;
    }

    public int getActiveTreatments() {
        return activeTreatments;
    }

    public void setActiveTreatments(
            int activeTreatments) {

        this.activeTreatments =
                activeTreatments;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(
            int totalAppointments) {

        this.totalAppointments =
                totalAppointments;
    }

    public int getScheduledAppointments() {
        return scheduledAppointments;
    }

    public void setScheduledAppointments(
            int scheduledAppointments) {

        this.scheduledAppointments =
                scheduledAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(
            int completedAppointments) {

        this.completedAppointments =
                completedAppointments;
    }

    public int getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(
            int cancelledAppointments) {

        this.cancelledAppointments =
                cancelledAppointments;
    }

    public int getTotalBills() {
        return totalBills;
    }

    public void setTotalBills(
            int totalBills) {

        this.totalBills =
                totalBills;
    }

    public int getPaidBills() {
        return paidBills;
    }

    public void setPaidBills(
            int paidBills) {

        this.paidBills =
                paidBills;
    }

    public int getUnpaidBills() {
        return unpaidBills;
    }

    public void setUnpaidBills(
            int unpaidBills) {

        this.unpaidBills =
                unpaidBills;
    }

    public BigDecimal getTotalBilled() {

        return totalBilled != null
                ? totalBilled
                : BigDecimal.ZERO;
    }

    public void setTotalBilled(
            BigDecimal totalBilled) {

        this.totalBilled =
                totalBilled;
    }

    public BigDecimal getTotalPaid() {

        return totalPaid != null
                ? totalPaid
                : BigDecimal.ZERO;
    }

    public void setTotalPaid(
            BigDecimal totalPaid) {

        this.totalPaid =
                totalPaid;
    }

    public BigDecimal getOutstandingAmount() {

        return outstandingAmount != null
                ? outstandingAmount
                : BigDecimal.ZERO;
    }

    public void setOutstandingAmount(
            BigDecimal outstandingAmount) {

        this.outstandingAmount =
                outstandingAmount;
    }
}
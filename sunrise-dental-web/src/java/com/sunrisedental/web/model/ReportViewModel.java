package com.sunrisedental.web.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ReportViewModel {

    private String fromDate;
    private String toDate;

    private int totalAppointments;
    private int scheduledAppointments;
    private int completedAppointments;
    private int cancelledAppointments;

    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal outstandingAmount;

    private List<DentistWorkload> dentistWorkload;
    private List<TreatmentDemand> treatmentDemand;

    public ReportViewModel() {

        this.totalBilled =
                BigDecimal.ZERO;

        this.totalPaid =
                BigDecimal.ZERO;

        this.outstandingAmount =
                BigDecimal.ZERO;

        this.dentistWorkload =
                new ArrayList<>();

        this.treatmentDemand =
                new ArrayList<>();
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(
            String fromDate) {

        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(
            String toDate) {

        this.toDate = toDate;
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

    public List<DentistWorkload>
            getDentistWorkload() {

        if (dentistWorkload == null) {
            dentistWorkload =
                    new ArrayList<>();
        }

        return dentistWorkload;
    }

    public void setDentistWorkload(
            List<DentistWorkload> dentistWorkload) {

        this.dentistWorkload =
                dentistWorkload;
    }

    public List<TreatmentDemand>
            getTreatmentDemand() {

        if (treatmentDemand == null) {
            treatmentDemand =
                    new ArrayList<>();
        }

        return treatmentDemand;
    }

    public void setTreatmentDemand(
            List<TreatmentDemand> treatmentDemand) {

        this.treatmentDemand =
                treatmentDemand;
    }

    /*
     * ------------------------------------------------------------
     * Analytical helper methods
     * ------------------------------------------------------------
     */

    public BigDecimal getCompletionRate() {

        if (totalAppointments <= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal
                .valueOf(completedAppointments)
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .divide(
                        BigDecimal.valueOf(
                                totalAppointments
                        ),
                        1,
                        RoundingMode.HALF_UP
                );
    }

    public BigDecimal getCancellationRate() {

        if (totalAppointments <= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal
                .valueOf(cancelledAppointments)
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .divide(
                        BigDecimal.valueOf(
                                totalAppointments
                        ),
                        1,
                        RoundingMode.HALF_UP
                );
    }

    public BigDecimal getCollectionRate() {

        BigDecimal billed =
                getTotalBilled();

        if (billed.compareTo(
                BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO;
        }

        return getTotalPaid()
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .divide(
                        billed,
                        1,
                        RoundingMode.HALF_UP
                );
    }

    /*
     * ============================================================
     * Dentist Workload
     * ============================================================
     */

    public static class DentistWorkload {

        private int dentistId;
        private String dentistName;
        private String specialization;

        private int totalAppointments;
        private int completedAppointments;
        private int cancelledAppointments;

        public DentistWorkload() {
        }

        public int getDentistId() {
            return dentistId;
        }

        public void setDentistId(
                int dentistId) {

            this.dentistId =
                    dentistId;
        }

        public String getDentistName() {
            return dentistName;
        }

        public void setDentistName(
                String dentistName) {

            this.dentistName =
                    dentistName;
        }

        public String getSpecialization() {
            return specialization;
        }

        public void setSpecialization(
                String specialization) {

            this.specialization =
                    specialization;
        }

        public int getTotalAppointments() {
            return totalAppointments;
        }

        public void setTotalAppointments(
                int totalAppointments) {

            this.totalAppointments =
                    totalAppointments;
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

        public int getScheduledAppointments() {

            int scheduled =
                    totalAppointments
                    - completedAppointments
                    - cancelledAppointments;

            return Math.max(
                    scheduled,
                    0
            );
        }

        public BigDecimal getCompletionRate() {

            if (totalAppointments <= 0) {
                return BigDecimal.ZERO;
            }

            return BigDecimal
                    .valueOf(
                            completedAppointments
                    )
                    .multiply(
                            BigDecimal.valueOf(100)
                    )
                    .divide(
                            BigDecimal.valueOf(
                                    totalAppointments
                            ),
                            1,
                            RoundingMode.HALF_UP
                    );
        }
    }

    /*
     * ============================================================
     * Treatment Demand
     * ============================================================
     */

    public static class TreatmentDemand {

        private int treatmentId;
        private String treatmentCode;
        private String treatmentName;

        private int appointmentCount;

        private BigDecimal billedAmount;

        public TreatmentDemand() {

            this.billedAmount =
                    BigDecimal.ZERO;
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

        public int getAppointmentCount() {
            return appointmentCount;
        }

        public void setAppointmentCount(
                int appointmentCount) {

            this.appointmentCount =
                    appointmentCount;
        }

        public BigDecimal getBilledAmount() {

            return billedAmount != null
                    ? billedAmount
                    : BigDecimal.ZERO;
        }

        public void setBilledAmount(
                BigDecimal billedAmount) {

            this.billedAmount =
                    billedAmount;
        }
    }
}
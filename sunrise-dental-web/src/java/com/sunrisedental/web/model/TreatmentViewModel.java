package com.sunrisedental.web.model;

import java.math.BigDecimal;

public class TreatmentViewModel {

    private int treatmentId;

    private String treatmentCode;

    private String treatmentName;

    private String description;

    private BigDecimal treatmentPrice;

    private BigDecimal consultationFee;

    private int estimatedDurationMinutes;

    private boolean active;


    public TreatmentViewModel() {
    }


    public int getTreatmentId() {
        return treatmentId;
    }


    public String getTreatmentCode() {
        return treatmentCode;
    }


    public String getTreatmentName() {
        return treatmentName;
    }


    public String getDescription() {
        return description;
    }


    public BigDecimal getTreatmentPrice() {

        return treatmentPrice == null
                ? BigDecimal.ZERO
                : treatmentPrice;
    }


    public BigDecimal getConsultationFee() {

        return consultationFee == null
                ? BigDecimal.ZERO
                : consultationFee;
    }


    public int getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }


    public boolean isActive() {
        return active;
    }


    /**
     * Standard charge before any later
     * billing adjustments or extra items.
     */
    public BigDecimal getStandardCharge() {

        return getTreatmentPrice()
                .add(
                        getConsultationFee()
                );
    }


    public String getStatusText() {

        return active
                ? "Active"
                : "Inactive";
    }


    public String getDurationText() {

        if (estimatedDurationMinutes <= 0) {
            return "Not specified";
        }

        if (estimatedDurationMinutes < 60) {

            return estimatedDurationMinutes
                    + " min";
        }

        int hours =
                estimatedDurationMinutes / 60;

        int minutes =
                estimatedDurationMinutes % 60;

        if (minutes == 0) {

            return hours == 1
                    ? "1 hr"
                    : hours + " hrs";
        }

        return hours
                + " hr "
                + minutes
                + " min";
    }
}
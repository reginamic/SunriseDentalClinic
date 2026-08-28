package com.sunrisedental.api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Treatment {

    private int treatmentId;
    private String treatmentCode;
    private String treatmentName;
    private String description;
    private BigDecimal treatmentPrice;
    private BigDecimal consultationFee;
    private Integer estimatedDurationMinutes;
    private boolean active;
    private LocalDateTime createdAt;

    public Treatment() {
    }

    public Treatment(int treatmentId,
                     String treatmentCode,
                     String treatmentName,
                     String description,
                     BigDecimal treatmentPrice,
                     BigDecimal consultationFee,
                     Integer estimatedDurationMinutes,
                     boolean active,
                     LocalDateTime createdAt) {

        this.treatmentId = treatmentId;
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.description = description;
        this.treatmentPrice = treatmentPrice;
        this.consultationFee = consultationFee;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.active = active;
        this.createdAt = createdAt;
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

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTreatmentPrice() {
        return treatmentPrice;
    }

    public void setTreatmentPrice(BigDecimal treatmentPrice) {
        this.treatmentPrice = treatmentPrice;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
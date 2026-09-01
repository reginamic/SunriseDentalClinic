package com.sunrisedental.api.model;

import java.time.LocalDateTime;

public class Dentist {

    private int dentistId;
    private String dentistCode;
    private String fullName;
    private String specialization;
    private String contactNumber;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;

    public Dentist() {
    }

    public Dentist(int dentistId,
                   String dentistCode,
                   String fullName,
                   String specialization,
                   String contactNumber,
                   String email,
                   boolean active,
                   LocalDateTime createdAt) {

        this.dentistId = dentistId;
        this.dentistCode = dentistCode;
        this.fullName = fullName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
        this.active = active;
        this.createdAt = createdAt;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
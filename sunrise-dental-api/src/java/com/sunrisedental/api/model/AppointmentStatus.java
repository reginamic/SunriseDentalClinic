package com.sunrisedental.api.model;

public enum AppointmentStatus {

    SCHEDULED,
    COMPLETED,
    CANCELLED;

    public static AppointmentStatus fromString(String value) {

        if (value == null || value.isBlank()) {
            return SCHEDULED;
        }

        try {
            return AppointmentStatus.valueOf(
                    value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid appointment status: " + value
            );
        }
    }
}
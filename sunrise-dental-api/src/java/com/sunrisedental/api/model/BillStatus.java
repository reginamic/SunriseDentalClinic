package com.sunrisedental.api.model;

public enum BillStatus {

    UNPAID,
    PAID,
    CANCELLED;

    public static BillStatus fromString(String value) {

        if (value == null || value.isBlank()) {
            return UNPAID;
        }

        try {
            return BillStatus.valueOf(
                    value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid bill status: " + value
            );
        }
    }
}
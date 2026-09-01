package com.sunrisedental.api.model;

public enum BillItemType {

    TREATMENT,
    CONSULTATION,
    EXTRA;

    public static BillItemType fromString(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Bill item type is required."
            );
        }

        try {
            return BillItemType.valueOf(
                    value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid bill item type: " + value
            );
        }
    }
}
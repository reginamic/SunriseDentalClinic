package com.sunrisedental.api.pattern.factory;

import com.sunrisedental.api.model.BillItem;
import com.sunrisedental.api.model.BillItemType;

import java.math.BigDecimal;

public class BillItemFactory {

    public BillItem createTreatmentCharge(
            String treatmentName,
            BigDecimal treatmentPrice) {

        return createItem(
                treatmentName,
                BillItemType.TREATMENT,
                1,
                treatmentPrice
        );
    }

    public BillItem createConsultationCharge(
            BigDecimal consultationFee) {

        return createItem(
                "Consultation Fee",
                BillItemType.CONSULTATION,
                1,
                consultationFee
        );
    }

    public BillItem createAdditionalCharge(
            String chargeName,
            int quantity,
            BigDecimal unitPrice) {

        return createItem(
                chargeName,
                BillItemType.EXTRA,
                quantity,
                unitPrice
        );
    }

    private BillItem createItem(
            String itemName,
            BillItemType itemType,
            int quantity,
            BigDecimal unitPrice) {

        validateItem(
                itemName,
                itemType,
                quantity,
                unitPrice
        );

        BillItem item = new BillItem();

        item.setItemName(itemName.trim());
        item.setItemType(itemType);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);

        item.calculateTotal();

        return item;
    }

    private void validateItem(
            String itemName,
            BillItemType itemType,
            int quantity,
            BigDecimal unitPrice) {

        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException(
                    "Bill item name is required."
            );
        }

        if (itemType == null) {
            throw new IllegalArgumentException(
                    "Bill item type is required."
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Bill item quantity must be greater than zero."
            );
        }

        if (unitPrice == null) {
            throw new IllegalArgumentException(
                    "Bill item price is required."
            );
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Bill item price cannot be negative."
            );
        }
    }
}
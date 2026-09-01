package com.sunrisedental.api.pattern.composite;

import com.sunrisedental.api.model.BillItem;

import java.math.BigDecimal;
import java.util.Objects;

public class BillCharge implements BillingComponent {

    private final BillItem billItem;

    public BillCharge(BillItem billItem) {

        this.billItem = Objects.requireNonNull(
                billItem,
                "BillItem cannot be null."
        );
    }

    @Override
    public String getName() {
        return billItem.getItemName();
    }

    @Override
    public BigDecimal getAmount() {

        BigDecimal amount =
                billItem.getTotalPrice();

        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }

    public BillItem getBillItem() {
        return billItem;
    }
}
package com.sunrisedental.api.pattern.decorator;

import com.sunrisedental.api.pattern.composite.BillingComponent;

import java.math.BigDecimal;
import java.util.Objects;

public class DiscountDecorator
        extends BillingDecorator {

    private final String discountName;
    private final BigDecimal discountAmount;

    public DiscountDecorator(
            BillingComponent component,
            String discountName,
            BigDecimal discountAmount) {

        super(component);

        if (discountName == null || discountName.isBlank()) {
            throw new IllegalArgumentException(
                    "Discount name is required."
            );
        }

        Objects.requireNonNull(
                discountAmount,
                "Discount amount is required."
        );

        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Discount amount cannot be negative."
            );
        }

        this.discountName = discountName.trim();
        this.discountAmount = discountAmount;
    }

    @Override
    public String getName() {
        return component.getName()
                + " - "
                + discountName;
    }

    @Override
    public BigDecimal getAmount() {

        BigDecimal discountedAmount =
                component
                        .getAmount()
                        .subtract(discountAmount);

        if (discountedAmount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return discountedAmount;
    }

    public String getDiscountName() {
        return discountName;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
}
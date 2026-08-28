package com.sunrisedental.api.pattern.decorator;

import com.sunrisedental.api.pattern.composite.BillingComponent;

import java.math.BigDecimal;
import java.util.Objects;

public class AdditionalChargeDecorator
        extends BillingDecorator {

    private final String chargeName;
    private final BigDecimal additionalCharge;

    public AdditionalChargeDecorator(
            BillingComponent component,
            String chargeName,
            BigDecimal additionalCharge) {

        super(component);

        if (chargeName == null || chargeName.isBlank()) {
            throw new IllegalArgumentException(
                    "Additional charge name is required."
            );
        }

        Objects.requireNonNull(
                additionalCharge,
                "Additional charge amount is required."
        );

        if (additionalCharge.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Additional charge cannot be negative."
            );
        }

        this.chargeName = chargeName.trim();
        this.additionalCharge = additionalCharge;
    }

    @Override
    public String getName() {
        return component.getName()
                + " + "
                + chargeName;
    }

    @Override
    public BigDecimal getAmount() {

        return component
                .getAmount()
                .add(additionalCharge);
    }

    public String getChargeName() {
        return chargeName;
    }

    public BigDecimal getAdditionalCharge() {
        return additionalCharge;
    }
}
package com.sunrisedental.api.pattern.decorator;

import com.sunrisedental.api.pattern.composite.BillingComponent;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class BillingDecorator
        implements BillingComponent {

    protected final BillingComponent component;

    protected BillingDecorator(
            BillingComponent component) {

        this.component = Objects.requireNonNull(
                component,
                "Billing component cannot be null."
        );
    }

    @Override
    public String getName() {
        return component.getName();
    }

    @Override
    public BigDecimal getAmount() {
        return component.getAmount();
    }

    public BillingComponent getWrappedComponent() {
        return component;
    }
}
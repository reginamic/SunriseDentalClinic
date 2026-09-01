package com.sunrisedental.api.pattern.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BillingGroup implements BillingComponent {

    private final String name;
    private final List<BillingComponent> components;

    public BillingGroup(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Billing group name is required."
            );
        }

        this.name = name.trim();
        this.components = new ArrayList<>();
    }

    public BillingGroup add(BillingComponent component) {

        components.add(
                Objects.requireNonNull(
                        component,
                        "Billing component cannot be null."
                )
        );

        return this;
    }

    public boolean remove(BillingComponent component) {
        return components.remove(component);
    }

    public List<BillingComponent> getComponents() {
        return Collections.unmodifiableList(
                components
        );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getAmount() {

        return components.stream()
                .map(BillingComponent::getAmount)
                .filter(Objects::nonNull)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}
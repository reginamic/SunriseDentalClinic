package com.sunrisedental.api.pattern.composite;

import java.math.BigDecimal;

public interface BillingComponent {

    String getName();

    BigDecimal getAmount();
}
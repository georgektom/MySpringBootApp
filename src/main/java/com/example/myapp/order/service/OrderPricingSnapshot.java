package com.example.myapp.order.service;

import java.math.BigDecimal;

public record OrderPricingSnapshot(
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        long totalPriceInMinorUnits,
        String currency
) {
}


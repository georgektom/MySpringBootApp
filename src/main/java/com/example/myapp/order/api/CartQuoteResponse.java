package com.example.myapp.order.api;

import java.math.BigDecimal;

public record CartQuoteResponse(
        String productCode,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        long totalPriceInMinorUnits,
        String currency
) {
}


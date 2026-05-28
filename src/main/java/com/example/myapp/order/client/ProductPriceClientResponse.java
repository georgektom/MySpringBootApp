package com.example.myapp.order.client;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductPriceClientResponse(
        String productId,
        BigDecimal price,
        String currency,
        Long priceInMinorUnits,
        boolean active,
        Instant importedAt,
        String source
) {
}


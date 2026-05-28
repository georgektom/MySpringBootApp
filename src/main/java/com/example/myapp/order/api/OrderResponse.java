package com.example.myapp.order.api;

import com.example.myapp.order.domain.OrderStatus;

import java.time.Instant;

public record OrderResponse(
        Long id,
        String customerName,
        String productCode,
        int quantity,
        OrderStatus status,
        String s3ObjectKey,
        Instant createdAt
) {
}


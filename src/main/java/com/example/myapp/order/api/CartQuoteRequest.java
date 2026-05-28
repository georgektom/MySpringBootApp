package com.example.myapp.order.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CartQuoteRequest(
        @NotBlank String productCode,
        @Min(1) int quantity
) {
}


package com.example.myapp.order.service;

import com.example.myapp.order.client.PricingClient;
import com.example.myapp.order.client.ProductPriceClientResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderPricingService {

    private final PricingClient pricingClient;

    public OrderPricingService(PricingClient pricingClient) {
        this.pricingClient = pricingClient;
    }

    public OrderPricingSnapshot calculate(String productCode, int quantity) {
        ProductPriceClientResponse price = pricingClient.getPrice(productCode);
        BigDecimal totalPrice = price.price().multiply(BigDecimal.valueOf(quantity));

        return new OrderPricingSnapshot(
                price.price(),
                totalPrice,
                price.priceInMinorUnits() * quantity,
                price.currency()
        );
    }
}


package com.example.myapp.order.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PricingClient {

    private final RestClient pricingRestClient;

    public PricingClient(RestClient pricingRestClient) {
        this.pricingRestClient = pricingRestClient;
    }

    public ProductPriceClientResponse getPrice(String productId) {
        ProductPriceClientResponse response = pricingRestClient.get()
                .uri("/api/prices/{productId}", productId)
                .retrieve()
                .body(ProductPriceClientResponse.class);

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Pricing service returned an empty response");
        }

        return response;
    }
}


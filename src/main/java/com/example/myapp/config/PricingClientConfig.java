package com.example.myapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PricingClientConfig {

    @Bean
    RestClient pricingRestClient(
            RestClient.Builder builder,
            PricingServiceProperties pricingServiceProperties
    ) {
        return builder
                .baseUrl(pricingServiceProperties.getBaseUrl())
                .build();
    }
}

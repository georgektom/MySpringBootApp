package com.example.myapp.order.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.myapp.config.AppAwsProperties;

@Component
public class OrderMessageListener {

    private static final Logger log = LoggerFactory.getLogger(OrderMessageListener.class);

    private final OrderService orderService;
    private final AppAwsProperties awsProperties;

    public OrderMessageListener(OrderService orderService, AppAwsProperties awsProperties) {
        this.orderService = orderService;
        this.awsProperties = awsProperties;
    }

    @SqsListener("${app.aws.sqs.order-queue}")
    public void handle(OrderProcessingMessage message) {
        log.info("Received message from queue {} for order {}", awsProperties.getSqs().getOrderQueue(), message.orderId());
        orderService.markProcessed(message.orderId());
    }
}


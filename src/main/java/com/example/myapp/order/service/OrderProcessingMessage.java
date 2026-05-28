package com.example.myapp.order.service;

public record OrderProcessingMessage(Long orderId, String eventType) {
}


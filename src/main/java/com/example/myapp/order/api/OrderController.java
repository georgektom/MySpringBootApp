package com.example.myapp.order.api;

import com.example.myapp.order.domain.OrderEntity;
import com.example.myapp.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return toResponse(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return toResponse(orderService.getOrder(id));
    }

    @PostMapping("/quote")
    public CartQuoteResponse quoteOrder(@Valid @RequestBody CartQuoteRequest request) {
        return orderService.quoteOrder(request);
    }

    private OrderResponse toResponse(OrderEntity entity) {
        return new OrderResponse(
                entity.getId(),
                entity.getCustomerName(),
                entity.getProductCode(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getTotalPrice(),
                entity.getTotalPriceInMinorUnits(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getS3ObjectKey(),
                entity.getCreatedAt()
        );
    }
}

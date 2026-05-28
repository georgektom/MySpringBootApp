package com.example.myapp.order.graphql;

import com.example.myapp.order.domain.OrderEntity;
import com.example.myapp.order.service.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OrderGraphqlController {

    private final OrderService orderService;

    public OrderGraphqlController(OrderService orderService) {
        this.orderService = orderService;
    }

    @QueryMapping
    public OrderEntity order(@Argument Long id) {
        return orderService.getOrder(id);
    }

    @QueryMapping
    public List<OrderEntity> orders() {
        return orderService.getOrders();
    }
}


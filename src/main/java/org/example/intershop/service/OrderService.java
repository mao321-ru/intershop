package org.example.intershop.service;

import org.example.intershop.dto.OrderDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    public static record Orders(List<OrderDto> orders, BigDecimal total) {}

    Mono<OrderDto> getOrder(Long orderId);

    Mono<Orders> findOrders();

}

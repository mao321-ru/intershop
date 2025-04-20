package org.example.intershop.service;

import org.example.intershop.dto.OrderDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    record Orders(List<OrderDto> orders, BigDecimal total) {}

    Mono<OrderDto> getOrder(Long orderId, String userLogin);

    Mono<Orders> findOrders( String userLogin);

}

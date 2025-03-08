package org.example.intershop.service;

import org.example.intershop.dto.OrderDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    public static record Orders(List<OrderDto> orders, BigDecimal total) {}

    Optional<OrderDto> getOrder(Long orderId);

    Orders findOrders();

}

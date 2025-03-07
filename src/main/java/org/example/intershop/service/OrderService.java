package org.example.intershop.service;

import org.example.intershop.dto.OrderDto;
import org.example.intershop.dto.ProductDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<OrderDto> getOrder(Long orderId);

    List<OrderDto> findOrders();

}

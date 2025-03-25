package org.example.intershop.repository;

import org.example.intershop.model.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
}

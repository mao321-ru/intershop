package org.example.intershop.repository;

import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

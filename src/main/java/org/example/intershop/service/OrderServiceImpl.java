package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.example.intershop.dto.OrderDto;
import org.example.intershop.mapper.OrderMapper;
import org.example.intershop.repository.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repo;

    @Override
    public Optional<OrderDto> getOrder(Long orderId) {
        return repo.findById( orderId).map( OrderMapper::toOrderDto);
    }

    @Override
    public Orders findOrders() {
        List<OrderDto> orders = repo.findAll( Sort.by( "number")).stream().map( OrderMapper::toOrderDto).toList();
        return new Orders(
            orders,
            orders.stream().map( OrderDto::getTotal).reduce( BigDecimal.ZERO, BigDecimal::add)
        );
    }
}

package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.dto.OrderDto;
import org.example.intershop.dto.ProductDto;
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
    public List<OrderDto> findOrders() {
        return repo.findAll().stream().map( OrderMapper::toOrderDto).toList();
    }
}

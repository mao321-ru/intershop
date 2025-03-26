package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.dto.OrderDto;
import org.example.intershop.mapper.OrderMapper;
import org.example.intershop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repo;

    @Override
    public Mono<OrderDto> getOrder( Long orderId) {
        return repo.findById( orderId)
            .flatMap( ord ->
                repo.findOrderProductByOrderId( orderId)
                    .collectList()
                    .map( opl -> {
                        ord.setProducts( opl);
                        return ord;
                    })
            )
            .map( OrderMapper::toOrderDto);
    }

    @Override
    public Mono<Orders> findOrders() {
        return repo.findAll()
            .flatMap( ord ->
                repo.findOrderProductByOrderId( ord.getId())
                    .collectList()
                    .map( opl -> {
                        ord.setProducts( opl);
                        return OrderMapper.toOrderDto( ord);
                    })
            )
            .collectList()
            .map( orders -> {
                // нужно сортировать здесь, т.к. .flatMap выполняется параллельно и порядок заказов в списке случаен
                orders.sort( Comparator.comparingLong( OrderDto::getOrderNumber));
                return
                    new Orders(
                        orders,
                        orders.stream().map( OrderDto::getTotal).reduce( BigDecimal.ZERO, BigDecimal::add)
                    );
            })
        ;
    }
}

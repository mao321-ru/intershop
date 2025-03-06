package org.example.intershop.mapper;

import org.example.intershop.dto.OrderDto;
import org.example.intershop.dto.OrderProductDto;
import org.example.intershop.model.Order;
import org.example.intershop.model.OrderProduct;
import org.example.intershop.model.Product;

import java.math.BigDecimal;

public class OrderMapper {

    public static OrderProductDto toOrderProductDto( OrderProduct op) {
        Product p = op.getProduct();
        return OrderProductDto.builder()
                .productId( p.getId())
                .productName( p.getName())
                .quantity( op.getQuantity())
                .amount( op.getAmount())
                .isImage( p.getImage() != null)
                .build();
    }

    public static OrderDto toOrderDto(Order o) {
        return OrderDto.builder()
                .orderNumber( o.getNumber())
                .products(
                    o.getProducts().stream()
                        .map( OrderMapper::toOrderProductDto)
                        .toList()
                )
                .total(
                    o.getProducts().stream()
                        .map( OrderProduct::getAmount)
                        .reduce( BigDecimal.ZERO, BigDecimal::add)
                )
                .build();
    }
}

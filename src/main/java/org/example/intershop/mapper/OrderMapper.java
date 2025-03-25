package org.example.intershop.mapper;

import org.example.intershop.dto.OrderDto;
import org.example.intershop.dto.OrderProductDto;
import org.example.intershop.model.Order;
import org.example.intershop.model.OrderProduct;
import org.example.intershop.model.Product;

import java.math.BigDecimal;
import java.util.Comparator;

public class OrderMapper {

    public static OrderProductDto toOrderProductDto( OrderProduct op) {
        return OrderProductDto.builder()
                .productId( op.getProductId())
                .productName( op.getProductName())
                .quantity( op.getQuantity())
                .amount( op.getAmount())
                .isImage( op.getImageId() != null)
                .build();
    }

    public static OrderDto toOrderDto(Order o) {
        return OrderDto.builder()
                .orderId( o.getId())
                .orderNumber( o.getNumber())
                .total( o.getTotal())
                .products(
                    o.getProducts().stream()
                        .map( OrderMapper::toOrderProductDto)
                        .toList()
                )
                .build();
    }
}

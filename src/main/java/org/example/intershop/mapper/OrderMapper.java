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
//        Product p = op.getProduct();
        return OrderProductDto.builder()
//                .productId( p.getId())
//                .productName( p.getName())
                .quantity( op.getQuantity())
                .amount( op.getAmount())
//                .isImage( p.getImage() != null)
                .build();
    }

    public static OrderDto toOrderDto(Order o) {
        return OrderDto.builder()
                .orderId( o.getId())
                .orderNumber( o.getNumber())
                .total( o.getTotal())
//                .products(
//                    o.getProducts().stream()
//                        .map( OrderMapper::toOrderProductDto)
//                        .sorted( Comparator.comparing( OrderProductDto::getProductName))
//                        .toList()
//                )
                .build();
    }
}

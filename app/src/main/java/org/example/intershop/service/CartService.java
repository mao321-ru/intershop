package org.example.intershop.service;

import org.example.intershop.dto.ProductDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    record CartProducts(
            List<ProductDto> products,
            BigDecimal total
    ) {}

    record Cart(
        List<ProductDto> products,
        BigDecimal total,
        boolean buyEnabled,
        String buyDisabledReason
    ) {}

    Mono<CartProducts> findCartProducts();

    Mono<Cart> getCart( Mono<CartProducts> cartProducts);

    Mono<Long> buy();

}

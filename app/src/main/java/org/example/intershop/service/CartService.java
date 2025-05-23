package org.example.intershop.service;

import org.example.intershop.dto.ProductDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    record CartProducts(
            String userLogin,
            List<ProductDto> products,
            BigDecimal total
    ) {}

    record Cart(
        List<ProductDto> products,
        BigDecimal total,
        boolean buyEnabled,
        String buyDisabledReason
    ) {}

    Mono<CartProducts> findCartProducts( String userLogin);

    Mono<Cart> getCart( Mono<CartProducts> cartProducts);

    Mono<Long> buy( String userLogin);

}

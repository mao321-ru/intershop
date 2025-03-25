package org.example.intershop.service;

import org.example.intershop.dto.ProductDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    public static record CartInfo(List<ProductDto> products, BigDecimal total) {}

    Mono<CartInfo> findCartProducts();

    Mono<Long> buy();

}

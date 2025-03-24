package org.example.intershop.repository;

import org.example.intershop.model.CartProduct;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CartProductRepository extends R2dbcRepository<CartProduct, Long> {

    @Modifying
    @Query("UPDATE cart_products cp SET quantity = :quantity WHERE cp.product_id = :productId")
    Mono<Void> setQuantity( Long productId, Integer quantity);

    @Modifying
    @Query("DELETE from cart_products cp WHERE cp.product_id = :productId")
    Mono<Void> deleteByProductId( Long productId);

}

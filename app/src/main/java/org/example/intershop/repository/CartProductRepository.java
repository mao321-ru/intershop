package org.example.intershop.repository;

import org.example.intershop.model.CartProduct;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;


public interface CartProductRepository extends R2dbcRepository<CartProduct, Long> {

    @Modifying
    @Query("UPDATE cart_products cp SET quantity = :quantity WHERE cp.user_id = :userId and cp.product_id = :productId")
    Mono<Void> setQuantity( Long userId, Long productId, Integer quantity);

    @Modifying
    @Query("DELETE from cart_products cp WHERE cp.user_id = :userId and cp.product_id = :productId")
    Mono<Void> deleteProduct( Long userId, Long productId);

}

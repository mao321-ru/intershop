package org.example.intershop.repository;

import org.example.intershop.model.Image;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ImageRepository extends ReactiveCrudRepository<Image,Long> {

    @Query("SELECT i.* FROM products p JOIN images i ON i.image_id = p.image_id WHERE p.product_id = $1")
    Mono<Image> findByProductId( Long productId);

}

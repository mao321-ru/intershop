package org.example.intershop.repository;

import org.example.intershop.model.Product;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends R2dbcRepository<Product, Long> {

    Flux<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String nameSearch, String descSearch, Sort sort
    );

}

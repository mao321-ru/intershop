package org.example.intershop.repository;

import org.example.intershop.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//import org.springframework.data.jpa.repository.EntityGraph;
//import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends R2dbcRepository<Product, Long> {

    Flux<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameSearch, String descSearch, Pageable pageable);

}

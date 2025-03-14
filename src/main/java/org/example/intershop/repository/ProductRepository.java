package org.example.intershop.repository;

import org.example.intershop.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

//import org.springframework.data.jpa.repository.EntityGraph;
//import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends R2dbcRepository<Product, Long> {

//    Slice<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameSearch, String descSearch, Pageable pageable);
//
//    @EntityGraph( attributePaths = "image")
//    Optional<Product> findEntityGraphTypeFetchById( Long productId);

}

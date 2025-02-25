package org.example.intershop.repository;

import org.example.intershop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @NonNull
    Page<Product> findAll( Pageable pageable);

    @EntityGraph( attributePaths = "image")
    Optional<Product> findEntityGraphTypeFetchById( Long productId);

    // использовал только в тесте
    Product findByName(String name);

}

package org.example.intershop.repository;

import org.example.intershop.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    ProductImage findByProductId(long productId);
}

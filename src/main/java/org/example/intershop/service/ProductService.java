package org.example.intershop.service;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.model.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    Page<ProductDto> findProducts( Pageable pageable);

    void createProduct(ProductCreateDto dto);

    Optional<ProductImage> findProductImage(long productId);
}

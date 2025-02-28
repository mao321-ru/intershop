package org.example.intershop.service;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    Page<ProductDto> findProducts( Pageable pageable);

    void createProduct(ProductCreateDto dto);

    Optional<Image> findProductImage(long productId);

    void updateProduct(ProductUpdateDto pd);

    void deleteProduct(Long productId);
}

package org.example.intershop.service;

//import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
//import org.example.intershop.dto.ProductUpdateDto;
//import org.example.intershop.model.Image;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import reactor.core.publisher.Flux;

import java.util.Optional;

public interface ProductService {

    Flux<ProductDto> findProducts(Sort sort);

//    Slice<ProductDto> findProducts(String search, Pageable pageable);
//
//    Optional<ProductDto> getProduct(Long productId);
//
//    void createProduct(ProductCreateDto dto);
//
//    Optional<Image> findProductImage(long productId);
//
//    void updateProduct(ProductUpdateDto pd);
//
//    void deleteProduct(Long productId);
//
//    void changeInCartQuantity(long productId, Integer delta);
}

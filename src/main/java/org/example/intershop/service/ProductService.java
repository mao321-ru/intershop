package org.example.intershop.service;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.model.Image;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<ProductDto> findProducts(Sort sort);

    Mono<Slice<ProductDto>> findProducts(String search, Pageable pageable);

    Mono<ProductDto> getProduct(Long productId);

    Mono<ProductDto> createProduct(ProductCreateDto dto);

    Mono<Image> findProductImage(long productId);

    Mono<Boolean> updateProduct( ProductUpdateDto pd);

    Mono<Boolean> deleteProduct( Long productId);

//    void changeInCartQuantity(long productId, Integer delta);
}

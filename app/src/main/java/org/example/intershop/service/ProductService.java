package org.example.intershop.service;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.dto.SliceProductDto;
import org.example.intershop.model.Image;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<ProductDto> findProducts(Sort sort);

    Mono<SliceProductDto> findProducts(String search, Pageable pageable, String userLogin);

    Mono<ProductDto> getProduct(Long productId, String userLogin);

    Mono<ProductDto> createProduct(ProductCreateDto dto);

    Mono<Image> findProductImage(long productId);

    Mono<Boolean> updateProduct( ProductUpdateDto pd);

    Mono<Boolean> deleteProduct( Long productId);

    Mono<Void> changeInCartQuantity(String userLogin, Long productId, Integer delta);
}

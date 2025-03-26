package org.example.intershop.controller;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.net.URI;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final ProductService srv;

    @GetMapping( "/config")
    Mono<Rendering> configProducts() {
        log.debug( "configProducts");
        return Mono.just(
            Rendering.view( "config")
                .modelAttribute( "products", srv.findProducts( Sort.by("name")))
                .build()
        );
    }

    @PostMapping( "/config/products")
    public Mono<ResponseEntity<Void>> createProduct(ProductCreateDto pd) {
        log.debug( "createProduct");
        return srv.createProduct( pd)
            .map( it ->
                ResponseEntity.status( HttpStatus.FOUND)
                .location( URI.create( "/config"))
                .build()
            );
    }

    @PostMapping( "/config/products/{productId}")
    public Mono<ResponseEntity<Void>> changeProduct(@PathVariable Long productId, ProductUpdateDto pd) {
        log.debug(
            "changeProduct: productId=" + productId + ", method=" + pd.getMethod() + ", delImage=" + pd.getDelImage()
                + ", pd.getProductId=" + pd.getProductId()
        );
        return ( "delete".equals( pd.getMethod())
                    ? srv.deleteProduct( productId)
                    : srv.updateProduct( pd)
                ).map( deleted -> deleted
                    ? ResponseEntity.status( HttpStatus.FOUND)
                        .location( URI.create( "/config"))
                        .build()
                    : ResponseEntity.status( HttpStatus.NOT_FOUND)
                        .build()
                );
    }

}

package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.service.ProductService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService srv;

    @GetMapping( { "/products/{productId}"})
    Mono<Rendering> getProduct(
        @PathVariable Long productId,
        ServerWebExchange exchange
    ) {
        log.debug( "getProduct: productId: {}", productId);
        return exchange.getPrincipal()
            .map( Principal::getName)
            .defaultIfEmpty( "")
            .doOnNext( s -> log.debug( "userLogin: {}", s))
            .flatMap( userLogin -> srv.getProduct( productId, userLogin))
            .map( pr -> Rendering.view("item")
                    .modelAttribute( "pr", pr)
                    .build())
            .defaultIfEmpty( Rendering.view( "not_found")
                    .status( HttpStatus.NOT_FOUND)
                    .build());
    }

    @PostMapping( { "/products/{productId}"})
    public Mono<Void> changeInCartQuantity(
        @PathVariable long productId,
        ServerWebExchange exchange
    )
    {
        log.debug( "changeInCartQuantity: productId: {}", productId);
        return exchange.getFormData()
            .flatMap( mvm -> {
                final String action =  mvm.getFirst("action");
                log.debug( "action: `{}`", action);
                final Integer delta = ProductCartAction.valueOf( action.toUpperCase()).getDelta();
                var resp = exchange.getResponse();
                resp.setStatusCode( HttpStatus.FOUND);
                resp.getHeaders().setLocation( URI.create("/products/" + productId));
                return exchange.getPrincipal()
                    .map( Principal::getName)
                    .doOnNext( s -> log.debug( "userLogin: {}", s))
                    .flatMap( userLogin -> srv.changeInCartQuantity( userLogin, productId, delta))
                    .then( resp.setComplete());
            });
    }

    @GetMapping("/products/{productId}/image")
    @ResponseBody
    public Mono<ResponseEntity<InputStreamResource>> getProductImage(
        @PathVariable long productId
    ) {
        log.debug( "getProductImage: productId: {}", productId);
        return srv.findProductImage( productId)
            .map( img ->
                ResponseEntity.ok()
                    .contentLength( img.getFileData().length)
                    .contentType(MediaType.parseMediaType( img.getContentType()))
                    .body( new InputStreamResource( new ByteArrayInputStream( img.getFileData())))
            )
            .defaultIfEmpty( ResponseEntity.notFound().build());
    }

}

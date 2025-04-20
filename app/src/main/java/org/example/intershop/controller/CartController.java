package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.service.CartService;
import org.example.intershop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService srv;
    private final ProductService productSrv;

    @GetMapping( { "/cart"})
    Mono<String> findCartProducts(
        ServerWebExchange exchange,
        Model model
    ) {
        log.debug( "findCartProducts");
        return exchange.getPrincipal()
            .map( Principal::getName)
            .doOnNext( s -> log.debug( "userLogin: {}", s))
            .flatMap( userLogin -> srv.getCart( srv.findCartProducts( userLogin)))
            .map( cartInfo -> {
                model.addAttribute("cart", cartInfo);
                return "cart";
            });
    }

    @PostMapping( { "/cart/products/{productId}"})
    Mono<Void> changeQuantity(
        @PathVariable Long productId,
        ServerWebExchange exchange
    ) {
        log.debug( "changeQuantity: productId: {}", productId);
        return exchange.getFormData()
                .flatMap( mvm -> {
                    final String action =  mvm.getFirst("action");
                    log.debug( "action: `{}`", action);
                    final Integer delta = ProductCartAction.valueOf( action.toUpperCase()).getDelta();
                    var resp = exchange.getResponse();
                    resp.setStatusCode( HttpStatus.FOUND);
                    resp.getHeaders().setLocation( URI.create("/cart"));
                    return exchange.getPrincipal()
                        .map( Principal::getName)
                        .doOnNext( s -> log.debug( "userLogin: {}", s))
                        .flatMap( userLogin -> productSrv.changeInCartQuantity( userLogin, productId, delta))
                        .then( resp.setComplete());
                });
    }

    @PostMapping( { "/cart/buy"})
    Mono<ResponseEntity<Void>> buy(
        ServerWebExchange exchange
    ) {
        log.debug( "buy");
        return exchange.getPrincipal()
            .map( Principal::getName)
            .doOnNext( s -> log.debug( "userLogin: {}", s))
            .flatMap( srv::buy)
            .map( orderId ->
                ResponseEntity.status( HttpStatus.FOUND)
                    .location( URI.create( "/orders/" + orderId + "?isNew=1"))
                    .<Void>build()
            )
            .defaultIfEmpty( ResponseEntity.notFound().build())
        ;
    }

}

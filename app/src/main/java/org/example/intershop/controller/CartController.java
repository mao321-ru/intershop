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

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService srv;
    private final ProductService productSrv;

    @GetMapping( { "/cart"})
    Mono<String> findCartProducts(
        Model model
    ) {
        log.debug( "findCartProducts");
        return
            srv.getCart( srv.findCartProducts())
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
                    return productSrv.changeInCartQuantity( productId, delta)
                        .then( resp.setComplete());
                });
    }

    @PostMapping( { "/cart/buy"})
    Mono<ResponseEntity<Void>> buy() {
        log.debug( "buy");
        return srv.buy()
            .map( orderId ->
                ResponseEntity.status( HttpStatus.FOUND)
                    .location( URI.create( "/orders/" + orderId + "?isNew=1"))
                    .<Void>build()
            )
            .defaultIfEmpty( ResponseEntity.notFound().build())
        ;
    }

    // Более информационное сообщение об ошибке, т.ч. для ошибок платежного сервиса
    @ExceptionHandler( RuntimeException.class)
    public Mono<ResponseEntity<String>> handleException( RuntimeException e) {
        return Mono.just(
            ResponseEntity.internalServerError().body(
                e.getCause() != null
                    ? "%s: %s".formatted( e.getMessage(), e.getCause().getMessage())
                    : e.getMessage()
            )
        );
    }

}

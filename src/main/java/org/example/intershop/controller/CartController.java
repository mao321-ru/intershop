package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.model.Image;
import org.example.intershop.model.Product;
import org.example.intershop.service.CartService;
import org.example.intershop.service.ProductService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Optional;

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
            srv.findCartProducts()
            .map( cartInfo -> {
                model.addAttribute("products", cartInfo.products());
                model.addAttribute("total", cartInfo.total());
                return "cart";
            });
    }

    @PostMapping( { "/cart/products/{productId}"})
    Mono<Void> changeQuantity(
        @PathVariable Long productId,
        ServerWebExchange exchange
    ) {
        log.debug( "changeQuantity: productId: " + productId);
        return exchange.getFormData()
                .flatMap( mvm -> {
                    final String action =  mvm.getFirst("action");
                    log.debug( "action: " + action);
                    final Integer delta = ProductCartAction.valueOf( action.toUpperCase()).getDelta();
                    var resp = exchange.getResponse();
                    resp.setStatusCode( HttpStatus.FOUND);
                    resp.getHeaders().setLocation( URI.create("/cart"));
                    return productSrv.changeInCartQuantity( productId, delta)
                        .then( resp.setComplete());
                });
    }

 //   @PostMapping( { "/cart/buy"})
 //   String buy() {
 //       log.debug( "buy");
 //       long orderId = srv.buy();
 //       return "redirect:/orders/" + orderId + "?isNew=1";
 //   }
}

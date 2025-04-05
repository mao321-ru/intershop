package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService srv;

    @GetMapping( "/orders")
    Mono<String> findOrders(
        Model model
    ) {
        log.debug( "findOrders");
        return srv.findOrders()
            .map( o -> {
                model.addAttribute( "orders", o.orders());
                model.addAttribute( "total", o.total());
                return "orders";
            });
    }

    @GetMapping( { "/orders/{orderId}"})
    Mono<Rendering> getOrder(
        @PathVariable Long orderId,
        @RequestParam( defaultValue = "0") int isNew
    ) {
        log.debug( "getOrder: orderId: {}, isNew={}", orderId, isNew);
        return srv.getOrder( orderId)
            .map( ord ->
                Rendering.view("order")
                    .modelAttribute( "ord", ord)
                    .modelAttribute( "newOrder", isNew == 1)
                    .build()
            )
            .defaultIfEmpty(
                Rendering.view( "not_found")
                    .status( HttpStatus.NOT_FOUND)
                    .build()
            );
    }

}

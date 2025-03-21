package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.service.CartService;
import org.example.intershop.service.OrderService;
import org.example.intershop.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService srv;

    @GetMapping( "/orders")
    String findOrders(
            Model model
    ) {
        log.debug( "findOrders");
        var o = srv.findOrders();
        model.addAttribute( "orders", o.orders());
        model.addAttribute( "total", o.total());
        return "orders";
    }

    @GetMapping( { "/orders/{orderId}"})
    String getOrder(
        @PathVariable Long orderId,
        @RequestParam( defaultValue = "0") int isNew,
        Model model
    ) {
        log.debug( "getOrder: orderId: " + orderId + ", isNew=" + isNew);
        var ord = srv.getOrder( orderId).orElseThrow();
        model.addAttribute( "ord", ord);
        model.addAttribute( "newOrder", isNew == 1);
        return "order";
    }

}

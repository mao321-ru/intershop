package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.service.CartService;
import org.example.intershop.service.OrderService;
import org.example.intershop.service.ProductService;
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

    @GetMapping( { "/orders/{orderId}"})
    String getOrder(
        @PathVariable Long orderId,
        @RequestParam( required = false) Boolean isNew,
        Model model
    ) {
        log.debug( "getOrder: orderId: " + orderId);
        var order = srv.getOrder( orderId).orElseThrow();
        model.addAttribute( "order", order);
        model.addAttribute( "newOrder", isNew);
        return "order";
    }

}

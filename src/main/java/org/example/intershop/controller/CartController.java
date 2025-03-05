package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.model.Image;
import org.example.intershop.model.Product;
import org.example.intershop.service.CartService;
import org.example.intershop.service.ProductService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService srv;
    private final ProductService productSrv;

    @GetMapping( { "/cart"})
    String findCartProducts(
        Model model
    ) {
        log.debug( "findCartProducts");
        var cartInfo = srv.findCartProducts();
        model.addAttribute( "products", cartInfo.products());
        model.addAttribute( "total", cartInfo.total());
        return "cart";
    }

    @PostMapping( { "/cart/products/{productId}"})
    String changeQuantity(
        @PathVariable Long productId,
        @RequestParam String action
    ) {
        log.debug( "changeQuantity: productId: " + productId + ", action: " + action);
        productSrv.changeInCartQuantity( productId, ProductCartAction.valueOf( action.toUpperCase()).getDelta());
        return "redirect:/cart";
    }

}

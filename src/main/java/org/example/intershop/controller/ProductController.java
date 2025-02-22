package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService srv;

    @GetMapping( { "/", "/products"})
    String findProducts(
        Model model
    ) {
        var products = srv.findProducts( PageRequest.of( 0, 1000));
        model.addAttribute( "products", products);
        return "products";
    }

    @GetMapping( "/config")
    String configProducts(
        Model model
    ) {
        var products = srv.findProducts( PageRequest.of( 0, 1000));
        model.addAttribute( "products", products);
        return "config";
    }

    @PostMapping( "/config")
    public String createProduct( ProductCreateDto pd) {
        srv.createProduct( pd);
        return "redirect:/config";
    }

}

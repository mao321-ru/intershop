package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.model.Image;
import org.example.intershop.service.ProductService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final ProductService srv;

    @GetMapping( "/config")
    String configProducts(
        Model model
    ) {
        var products = srv.findProducts( PageRequest.of( 0, 1000));
        model.addAttribute( "products", products);
        return "config";
    }

    @PostMapping( "/config/products")
    public String createProduct( ProductCreateDto pd) {
        srv.createProduct( pd);
        return "redirect:/config";
    }

    @PostMapping(value = "/config/products/{productId}")
    public String updateProduct( ProductUpdateDto pd) {
        srv.updateProduct( pd);
        return "redirect:/config";
    }

    @PostMapping(value = "/config/products/{productId}", params = "_method=delete")
    public String deleteProduct( @PathVariable Long productId) {
        srv.deleteProduct( productId);
        return "redirect:/config";
    }

}

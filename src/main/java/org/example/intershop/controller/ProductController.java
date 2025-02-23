package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.model.Image;
import org.example.intershop.service.ProductService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.util.Optional;

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

    @GetMapping("/products/{productId}/image")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getProductImage(@PathVariable("productId") long productId) {
        Optional<Image> optImg = srv.findProductImage( productId);
        if ( optImg.isPresent()) {
            var img = optImg.get();
            return ResponseEntity.ok()
                    .contentLength( img.getFileData().length)
                    .contentType(MediaType.parseMediaType( img.getContentType()))
                    .body( new InputStreamResource( new ByteArrayInputStream( img.getFileData())));
        }
        else {
            return ResponseEntity.notFound().build();
        }
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

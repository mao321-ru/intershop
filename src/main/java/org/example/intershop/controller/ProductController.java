package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.model.Image;
import org.example.intershop.model.Product;
import org.example.intershop.service.ProductService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService srv;

    @GetMapping( { "/products/{productId}"})
    Mono<String> getProduct(
        @PathVariable Long productId,
        Model model
    ) {
        log.debug( "getProduct: productId: " + productId);
        return srv.getProduct( productId)
            .doOnNext( pr -> model.addAttribute( "pr", pr))
            .map( pr -> "item")
            .switchIfEmpty( Mono.error( new ResponseStatusException( HttpStatus.NOT_FOUND)));
    }

//    @PostMapping( { "/products/{productId}"})
//    String changeInCartQuantity(
//        @PathVariable Long productId,
//        @RequestParam String action
//    ) {
//        log.debug( "changeInCartQuantity: productId: " + productId + ", action: " + action);
//        srv.changeInCartQuantity( productId, ProductCartAction.valueOf( action.toUpperCase()).getDelta());
//        return "redirect:/products/" + productId;
//    }

    @GetMapping("/products/{productId}/image")
    @ResponseBody
    public Mono<ResponseEntity<InputStreamResource>> getProductImage(
        @PathVariable long productId
    ) {
        log.debug( "getProductImage: productId: " + productId);
        return srv.findProductImage( productId)
            .map( img ->
                ResponseEntity.ok()
                    .contentLength( img.getFileData().length)
                    .contentType(MediaType.parseMediaType( img.getContentType()))
                    .body( new InputStreamResource( new ByteArrayInputStream( img.getFileData())))
            )
            .defaultIfEmpty( ResponseEntity.notFound().build());
    }

}

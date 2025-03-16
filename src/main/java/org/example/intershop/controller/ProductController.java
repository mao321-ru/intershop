package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.model.Image;
import org.example.intershop.model.Product;
import org.example.intershop.service.ProductService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.Optional;

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
        var pr = srv.getProduct( productId);
        model.addAttribute( "pr", pr);
        return Mono.just( "item");
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
//
//    @GetMapping("/products/{productId}/image")
//    @ResponseBody
//    public ResponseEntity<InputStreamResource> getProductImage(
//        @PathVariable long productId
//    ) {
//        log.debug( "getProductImage: productId: " + productId);
//        Optional<Image> optImg = srv.findProductImage( productId);
//        if ( optImg.isPresent()) {
//            var img = optImg.get();
//            return ResponseEntity.ok()
//                    .contentLength( img.getFileData().length)
//                    .contentType(MediaType.parseMediaType( img.getContentType()))
//                    .body( new InputStreamResource( new ByteArrayInputStream( img.getFileData())));
//        }
//        else {
//            return ResponseEntity.notFound().build();
//        }
//    }

}

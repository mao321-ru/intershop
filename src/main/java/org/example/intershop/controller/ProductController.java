package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.model.Image;
import org.example.intershop.model.Product;
import org.example.intershop.service.ProductService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import jakarta.validation.constraints.Min;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProductController {

    private final ProductService srv;

    @GetMapping( { "/"})
    String findProducts(
        @RequestParam( defaultValue = "") String search,
        @RequestParam( defaultValue = "ALPHA") ProductSort sort,
        @RequestParam( defaultValue = "10") @Min(1) Integer pageSize,
        @RequestParam( defaultValue = "0") @Min(0) Integer pageNumber,
        Model model
    ) {
        search = search.trim();
        var paging = srv.findProducts( search, PageRequest.of( pageNumber, pageSize, sort.getSortValue()));
        model.addAttribute( "search", search);
        model.addAttribute( "sort", sort.name());
        model.addAttribute( "paging", paging);
        model.addAttribute( "products", paging.get());
        return "main";
    }

    @PostMapping( { "/main/products/{productId}"})
    String changeInCartQuantity(
            @PathVariable long productId,
            @RequestParam ProductCartAction action,
            @RequestParam String search,
            @RequestParam String sort,
            @RequestParam String pageSize,
            @RequestParam String pageNumber,
            RedirectAttributes ra
    ) {
        srv.changeInCartQuantity( productId, action.getDelta());
        ra.addAttribute( "search", search);
        ra.addAttribute( "sort", sort);
        ra.addAttribute( "pageSize", pageSize);
        ra.addAttribute( "pageNumber", pageNumber);
        return "redirect:/";
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

}

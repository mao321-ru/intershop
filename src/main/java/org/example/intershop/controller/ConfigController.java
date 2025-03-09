package org.example.intershop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final ProductService srv;

    @GetMapping( "/config")
    String configProducts(
        Model model
    ) {
        log.debug( "configProducts");
        var products = srv.findProducts( "", Pageable.unpaged( Sort.by("name")));
        model.addAttribute( "products", products);
        return "config";
    }

    @PostMapping( "/config/products")
    public String createProduct( ProductCreateDto pd) {
        log.debug( "createProduct");
        srv.createProduct( pd);
        return "redirect:/config";
    }

    @PostMapping(value = "/config/products/{productId}")
    public String updateProduct( ProductUpdateDto pd) {
        log.debug( "updateProduct: productId=" + pd.getProductId());
        srv.updateProduct( pd);
        return "redirect:/config";
    }

    @PostMapping(value = "/config/products/{productId}", params = "_method=delete")
    public String deleteProduct( @PathVariable Long productId) {
        log.debug( "deleteProduct: productId=" + productId);
        srv.deleteProduct( productId);
        return "redirect:/config";
    }

}

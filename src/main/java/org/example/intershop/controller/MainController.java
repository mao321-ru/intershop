package org.example.intershop.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class MainController {

    private final ProductService srv;

    @GetMapping( { "/"})
    String findProducts(
        @RequestParam( defaultValue = "") String search,
        @RequestParam( defaultValue = "ALPHA") ProductSort sort,
        @RequestParam( defaultValue = "10") @Min(1) Integer pageSize,
        @RequestParam( defaultValue = "0") @Min(0) Integer pageNumber,
        Model model
    ) {
        log.debug( "findProducts: pageNumber: " + pageNumber);
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
            @RequestParam String action,
            @RequestParam String search,
            @RequestParam String sort,
            @RequestParam String pageSize,
            @RequestParam String pageNumber,
            RedirectAttributes ra
    ) {
        log.debug( "changeInCartQuantity: productId: " + productId + ", action: " + action);
        srv.changeInCartQuantity( productId, ProductCartAction.valueOf( action.toUpperCase()).getDelta());
        ra.addAttribute( "search", search);
        ra.addAttribute( "sort", sort);
        ra.addAttribute( "pageSize", pageSize);
        ra.addAttribute( "pageNumber", pageNumber);
        return "redirect:/";
    }

}

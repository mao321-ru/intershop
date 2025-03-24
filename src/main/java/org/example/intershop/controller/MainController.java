package org.example.intershop.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class MainController {

    private final ProductService srv;

    @GetMapping( { "/"})
    Mono<String> findProducts(
        @RequestParam( defaultValue = "") String search,
        @RequestParam( defaultValue = "ALPHA") ProductSort sort,
        @RequestParam( defaultValue = "10") @Min(1) Integer pageSize,
        @RequestParam( defaultValue = "0") @Min(0) Integer pageNumber,
        Model model
    ) {
        log.debug( "findProducts: pageNumber: " + pageNumber);
        final String searchStr = search.trim();
        return
            srv.findProducts( searchStr, PageRequest.of( pageNumber, pageSize, sort.getSortValue()))
            .map( paging -> {
                model.addAttribute( "search", searchStr);
                model.addAttribute( "sort", sort.name());
                model.addAttribute( "paging", paging);
                model.addAttribute( "products", paging.get());
                return "main";
            });
    }

    @PostMapping( { "/main/products/{productId}"})
    public Mono<Void> changeInCartQuantity( @PathVariable long productId, ServerWebExchange exchange)
    {
        log.debug( "changeInCartQuantity: productId: " + productId);
        return exchange.getFormData()
            .flatMap( mvm -> {
                final String action =  mvm.getFirst("action");
                log.debug( "action: " + action);
                return
                    srv.changeInCartQuantity( productId, ProductCartAction.valueOf( action.toUpperCase()).getDelta())
                        .thenReturn( mvm);
            })
            .flatMap( mvm -> {
                var resp = exchange.getResponse();
                resp.setStatusCode( HttpStatus.FOUND);
                resp.getHeaders().setLocation(
                    UriComponentsBuilder.fromPath( "/")
                        // сохраняем все параметры запроса кроме action
                        .queryParams( mvm)
                        .replaceQueryParam( "action")
                        .build()
                        .toUri()
                );
                return resp.setComplete();
            });
    }
}

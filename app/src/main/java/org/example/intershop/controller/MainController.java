package org.example.intershop.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.security.Principal;

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
        ServerWebExchange exchange,
        Model model
    ) {
        log.debug( "findProducts: pageNumber: {}", pageNumber);
        final String searchStr = search.trim();
        return exchange.getPrincipal()
            .map( Principal::getName)
            .defaultIfEmpty( "")
            .doOnNext( s -> log.debug( "userLogin: {}", s))
            .flatMap( userLogin -> srv.findProducts( searchStr, PageRequest.of( pageNumber, pageSize, sort.getSortValue()), userLogin))
            .map( paging -> {
                model.addAttribute( "search", searchStr);
                model.addAttribute( "sort", sort.name());
                model.addAttribute( "paging", paging);
                model.addAttribute( "products", paging.getContent());
                return "main";
            });
    }

    @PostMapping( { "/main/products/{productId}"})
    public Mono<Void> changeInCartQuantity(
        @PathVariable long productId,
        ServerWebExchange exchange
    )
    {
        log.debug( "changeInCartQuantity: productId: {}", productId);
        return exchange.getFormData()
            .flatMap( mvm -> {
                final String action =  mvm.getFirst("action");
                log.debug( "action: `{}`", action);
                final Integer delta = ProductCartAction.valueOf( action.toUpperCase()).getDelta();
                return exchange.getPrincipal()
                    .map( Principal::getName)
                    .doOnNext( s -> log.debug( "userLogin: {}", s))
                    .flatMap( userLogin -> srv.changeInCartQuantity( userLogin, productId, delta))
                    .thenReturn( mvm);
            })
            .flatMap( mvm -> {
                var resp = exchange.getResponse();
                resp.setStatusCode( HttpStatus.FOUND);
                resp.getHeaders().setLocation(
                    UriComponentsBuilder.fromPath( "/")
                        // сохраняем все параметры запроса кроме action и _csrf
                        .queryParams( mvm)
                        .replaceQueryParam( "action")
                        .replaceQueryParam( "_csrf")
                        .build()
                        .toUri()
                );
                return resp.setComplete();
            });
    }
}

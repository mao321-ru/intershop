package org.example.paysrv.controller;

import org.example.paysrv.api.PaymentApi;
import org.example.paysrv.domain.Balance;
import org.example.paysrv.domain.Purchase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@RestController
@Slf4j
public class PaymentApiController implements PaymentApi {

    @Override
    public Mono<ResponseEntity<Balance>> getBalance(
            final ServerWebExchange exchange
    ) {
        log.debug( "getBalance");
        Balance balance = new Balance( BigDecimal.valueOf( 200.15));
        return Mono.just( ResponseEntity.ok( balance));
    }

    @Override
    public Mono<ResponseEntity<Void>> pay(
            Mono<Purchase> purchase,
            final ServerWebExchange exchange
    ) {
        return purchase
            .doOnNext( p -> log.debug( "pay: amount: {}", p.getAmount()))
            .thenReturn( ResponseEntity.ok().build());
    }

}

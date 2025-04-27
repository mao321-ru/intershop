package org.example.paysrv.controller;

import lombok.RequiredArgsConstructor;
import org.example.paysrv.api.PaymentApi;
import org.example.paysrv.domain.Balance;
import org.example.paysrv.domain.Purchase;
import org.example.paysrv.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentApiController implements PaymentApi {

    private final PaymentService srv;

    @Override
    @PreAuthorize( "hasRole('GET_BALANCE')")
    public Mono<ResponseEntity<Balance>> getBalance(
            String accountId,
            final ServerWebExchange exchange
    ) {
        Balance balance = srv.getBalance( accountId);
        log.debug( "getBalance: accountId: {}, amount: {}", accountId, balance.getAmount());
        return Mono.just( ResponseEntity.ok( balance));
    }

    @Override
    @PreAuthorize( "hasRole('PAY')")
    public Mono<ResponseEntity<Void>> pay(
            Mono<Purchase> purchase,
            final ServerWebExchange exchange
    ) {
        return purchase
            .doOnNext( p -> log.debug( "pay: accountId: {}, amount: {}", p.getAccountId(), p.getAmount()))
            .map( p-> {
                boolean isOk = srv.pay( p);
                log.debug( "pay successful: {}", isOk);
                return isOk
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.badRequest().build();
            });
    }

}

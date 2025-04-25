package org.example.paysrv.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.function.BiConsumer;

public class PaymentApiControllerTest extends ControllerTest {

    @Test
    void getBalance_noAuth() throws Exception {
        wtc.get().uri( "/api/balance")
                .exchange()
                .expectStatus().isEqualTo( HttpStatus.UNAUTHORIZED)
        ;
    }

    @Test
    void getBalance_checkRole() throws Exception {
        BiConsumer<String,Boolean> check = ( clientId, res) -> {
            wtc.get().uri( "/api/balance")
                .headers( headers -> headers.setBearerAuth( getAccessToken( clientId)))
                .exchange()
                .expectStatus().isEqualTo( res ? HttpStatus.OK : HttpStatus.FORBIDDEN)
            ;
        };
        check.accept(  "no_roles_tclient", false);
        check.accept(  "pay_only_tclient", false);
        check.accept(  "balance_only_tclient", true);
        check.accept(  "intershop", true);
    }

    @Test
    void getBalance_check() throws Exception {
        wtc.get().uri( "/api/balance")
            .headers( headers -> headers.setBearerAuth( getAccessToken()))
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType( MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath( "$.amount").isEqualTo( INIT_BALANCE)
        ;
    }

    @Test
    void pay_checkRole() throws Exception {
        BiConsumer<String,Boolean> check = ( clientId, res) -> {
            wtc.put().uri( "/api/pay")
                .headers( headers -> headers.setBearerAuth( getAccessToken( clientId)))
                .contentType( MediaType.APPLICATION_JSON)
                .bodyValue( "{\"amount\":%s}".formatted( 10))
                .exchange()
                .expectStatus().isEqualTo( res ? HttpStatus.OK : HttpStatus.FORBIDDEN)
            ;
        };
        check.accept(  "no_roles_tclient", false);
        check.accept(  "balance_only_tclient", false);
        check.accept(  "pay_only_tclient", true);
        check.accept(  "intershop", true);
    }

    @Test
    void pay_check() throws Exception {
        String accessToken = getAccessToken();
        BiConsumer<Double,Boolean> check = ( amt, res) -> {
            wtc.put().uri( "/api/pay")
                .headers( headers -> headers.setBearerAuth( accessToken))
                .contentType( MediaType.APPLICATION_JSON)
                .bodyValue( "{\"amount\":%s}".formatted( amt))
                .exchange()
                .expectStatus().isEqualTo( res ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
            ;
        };
        double balance = Double.parseDouble( INIT_BALANCE);
        // некорректное значение
        check.accept( - 1.0, false);
        check.accept( 0.005, false);
        check.accept(  1.01 , true);
        check.accept( 10.00 , true);
        // сумма платежа больше остатка на балансе
        check.accept( balance, false);
    }

}

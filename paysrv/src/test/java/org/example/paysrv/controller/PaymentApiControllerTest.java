package org.example.paysrv.controller;

import org.example.paysrv.domain.Balance;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

public class PaymentApiControllerTest extends ControllerTest {

    @Test
    void getBalance_check() throws Exception {
        wtc.get().uri( "/api/balance")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType( MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath( "$.amount").isEqualTo( INIT_BALANCE)
        ;
    }

    @Test
    void pay_check() throws Exception {
        BiConsumer<Double,Boolean> check = ( amt, res) -> {
            wtc.put().uri( "/api/pay")
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

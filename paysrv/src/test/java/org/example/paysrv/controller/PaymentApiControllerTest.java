package org.example.paysrv.controller;

import org.junit.jupiter.api.Disabled;
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
            wtc.get().uri( "/api/accounts/{accountId}/balance", "user")
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
        wtc.get().uri( "/api/accounts/{accountId}/balance", "user")
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
                .bodyValue( "{\"accountId\":\"%s\",\"amount\":%s}".formatted( "checkRoleAccount", 10))
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
                .bodyValue( "{\"accountId\":\"%s\",\"amount\":%s}".formatted( "checkAccount", amt))
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

    @Test
    void pay_noMoney() throws Exception {
        final double amount = Double.parseDouble( INIT_BALANCE) + 0.01;

        // превышение при первой попытке платежа
        wtc.put().uri( "/api/pay")
            .headers( headers -> headers.setBearerAuth( getAccessToken()))
            .contentType( MediaType.APPLICATION_JSON)
            .bodyValue( "{\"accountId\":\"%s\",\"amount\":%s}".formatted( "noMoneyAccount", amount))
            .exchange()
            .expectStatus().isEqualTo( HttpStatus.BAD_REQUEST)
            .expectBody().isEmpty()
        ;

        wtc.put().uri( "/api/pay")
                .headers( headers -> headers.setBearerAuth( getAccessToken()))
                .contentType( MediaType.APPLICATION_JSON)
                .bodyValue( "{\"accountId\":\"%s\",\"amount\":%s}".formatted( "noMoneyAccount", 0.01))
                .exchange()
                .expectStatus().isOk()
        ;

        // превышение после успешного платежа
        wtc.put().uri( "/api/pay")
                .headers( headers -> headers.setBearerAuth( getAccessToken()))
                .contentType( MediaType.APPLICATION_JSON)
                .bodyValue( "{\"accountId\":\"%s\",\"amount\":%s}".formatted( "noMoneyAccount", amount))
                .exchange()
                .expectStatus().isEqualTo( HttpStatus.BAD_REQUEST)
                .expectBody().isEmpty()
        ;
    }

    @Test
    void pay_accounts() throws Exception {
        String accessToken = getAccessToken();
        BiConsumer<String,Double> pay = ( accountId, amt) -> {
            wtc.put().uri( "/api/pay")
                    .headers( headers -> headers.setBearerAuth( accessToken))
                    .contentType( MediaType.APPLICATION_JSON)
                    .bodyValue( "{\"accountId\":\"%s\",\"amount\":%s}".formatted( accountId, amt))
                    .exchange()
                    .expectStatus().isEqualTo( HttpStatus.OK)
            ;
        };
        BiConsumer<String,Double> balance = ( accountId, amt) -> {
            wtc.get().uri("/api/accounts/{accountId}/balance", accountId)
                    .headers(headers -> headers.setBearerAuth( accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.amount").isEqualTo( amt)
            ;
        };
        final double initBalance = Double.parseDouble( INIT_BALANCE);
        // независимость операций по счетам
        balance.accept( "account1", initBalance);
        balance.accept( "account2", initBalance);
        pay.accept( "account1", initBalance);
        balance.accept( "account1", 0.0);
        balance.accept( "account2", initBalance);
    }


}

package org.example.intershop.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

public class OrderControllerTest extends ControllerTest {

    @Test
    void findOrders_noAuth() throws Exception {
        wtc.get().uri( "/orders")
            .exchange()
            .expectStatus().isFound()
            .expectHeader().valueEquals( "Location", "/login" )
        ;
    }

    @Test
    @WithMockUser( username = "user")
    void findOrders_check() throws Exception {
        wtc.get().uri( "/orders")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType( "text/html;charset=UTF-8")
            .expectBody()
            //.consumeWith( System.out::println) // вывод запроса и ответа
            .xpath( ORDERS_XPATH).nodeCount( Matchers.greaterThan( 0))
            .xpath( PRODUCTS_XPATH).nodeCount( Matchers.greaterThanOrEqualTo( EXISTS_ORDER_PRODUCT_COUNT))
            .xpath( ORDERS_TOTAL_XPATH).string( Matchers.containsString( EXISTS_ORDER_TOTAL.toString()))
            .xpath( TOTAL_XPATH).string( Matchers.containsString( EXISTS_ORDER_TOTAL.toString()))
        ;
    }

    @Test
    // у пользователя нет заказов
    @WithMockUser( username = "user3")
    void findOrders_noOrders() throws Exception {
        wtc.get().uri( "/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType( "text/html;charset=UTF-8")
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                .xpath( ORDERS_XPATH).nodeCount( 0)
        ;
    }

    @Test
    void getOrder_noAuth() throws Exception {
        wtc.get().uri( "/orders/{orderId}", USER_EXISTS_ORDER_ID)
            .exchange()
            .expectStatus().isFound()
            .expectHeader().valueEquals( "Location", "/login" )
        ;
    }

    @Test
    @WithMockUser( username = "user")
    void getOrder_check() throws Exception {
        wtc.get().uri( "/orders/{orderId}", USER_EXISTS_ORDER_ID)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType( "text/html;charset=UTF-8")
            .expectBody()
            //.consumeWith( System.out::println) // вывод запроса и ответа
            .xpath( PRODUCTS_XPATH).nodeCount( EXISTS_ORDER_PRODUCT_COUNT)
            .xpath( ORDERS_TOTAL_XPATH).string( Matchers.containsString( EXISTS_ORDER_TOTAL.toString()))
        ;
    }

    @Test
    @WithMockUser( username = "user3")
    void getOrder_anotherUserOrder() throws Exception {
        // заказ другого пользователя (user)
        wtc.get().uri( "/orders/{orderId}", USER_EXISTS_ORDER_ID)
                .exchange()
                .expectStatus().isNotFound()
        ;
    }

}

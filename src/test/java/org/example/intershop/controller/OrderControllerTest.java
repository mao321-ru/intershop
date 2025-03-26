package org.example.intershop.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class OrderControllerTest extends ControllerTest {

    @Test
    void findOrders_check() throws Exception {
        wtc.get().uri( "/orders")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType( "text/html")
            .expectBody()
            //.consumeWith( System.out::println) // вывод запроса и ответа
            .xpath( ORDERS_XPATH).nodeCount( Matchers.greaterThan( 0))
            .xpath( PRODUCTS_XPATH).nodeCount( Matchers.greaterThanOrEqualTo( EXISTS_ORDER_PRODUCT_COUNT))
            .xpath( ORDERS_TOTAL_XPATH).string( Matchers.containsString( EXISTS_ORDER_TOTAL.toString()))
            .xpath( TOTAL_XPATH).string( Matchers.containsString( EXISTS_ORDER_TOTAL.toString()))
        ;
    }

    @Test
    void getOrder_check() throws Exception {
        wtc.get().uri( "/orders/{orderId}", EXISTS_PRODUCT_ID)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType( "text/html")
            .expectBody()
            //.consumeWith( System.out::println) // вывод запроса и ответа
            .xpath( PRODUCTS_XPATH).nodeCount( EXISTS_ORDER_PRODUCT_COUNT)
            .xpath( ORDERS_TOTAL_XPATH).string( Matchers.containsString( EXISTS_ORDER_TOTAL.toString()))
        ;
    }

}

package org.example.intershop.controller;

import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Product;
import org.example.intershop.repository.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

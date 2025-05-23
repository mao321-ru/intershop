package org.example.intershop.controller;

import org.example.intershop.model.Order;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

public class CartControllerTest extends ControllerTest {

    @Test
    void changeQuantity_noAuth() throws Exception {
        wtc.mutateWith( csrf())
                .post().uri( "/cart/products/{productId}", EXISTS_PRODUCT_ID)
                .contentType( MediaType.APPLICATION_FORM_URLENCODED)
                .body( BodyInserters.fromFormData( "action", "plus"))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals( "Location", "/login" )
        ;
    }

    @Test
    @WithMockUser( username = "user")
    void changeQuantity_check() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        final String productName = EXISTS_PRODUCT_NAME;
        BigDecimal price = EXISTS_PRODUCT_PRICE;

        // изначально корзина пустая
        find( 0, " 0 ");

        // добавили товар в корзину
        changeQty( productId, "plus");
        find( 1, price.toString());

        // увеличили до 2 шт
        changeQty( productId, "plus");
        find( 1, price.multiply( BigDecimal.TWO).toString());

        // увеличили-уменьшили-удалили
        changeQty( productId, "plus");
        changeQty( productId, "minus");
        changeQty( productId, "delete");
        find( 0, " 0 ");
    }

    @Test
    void findCardProducts_noAuth() throws Exception {
        wtc.get().uri( "/cart")
            .exchange()
            .expectStatus().isFound()
            .expectHeader().valueEquals( "Location", "/login" )
        ;
    }

    @Test
    @WithMockUser( username = "user")
    void findCardProducts_buyEnabled() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        final String productName = EXISTS_PRODUCT_NAME;
        BigDecimal price = EXISTS_PRODUCT_PRICE;

        // добавили товар в корзину
        changeQty( productId, "plus");

        buyState( true);
    }

    @Test
    void buy_noAuth() throws Exception {
        wtc.mutateWith( csrf())
            .post().uri( "/cart/buy")
            .exchange()
            .expectStatus().isFound()
            .expectHeader().valueEquals( "Location", "/login" )
        ;
    }

    @Test
    @WithMockUser( username = "user")
    void buy_check() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        final BigDecimal price = EXISTS_PRODUCT_PRICE;
        final long orderId = TEMP_DATA_START_ID;

        wtc.mutateWith( csrf())
            .post().uri( "/cart/buy")
            .exchange()
            .expectStatus().isNotFound()
        ;

        changeQty( productId, "plus");

        wtc.mutateWith( csrf())
            .post().uri( "/cart/buy")
            .exchange()
            .expectStatus().isFound()
            .expectHeader().valueEquals( "Location", "/orders/" + orderId + "?isNew=1")
        ;

        Order order = getOrderById( orderId);
        assertNotNull( "Order not found", order);
        assertEquals( "Unexpected order number", (Long) TEMP_DATA_START_ID, order.getNumber());
        assertEquals( "Unexpected order total", price, order.getTotal());

        // корзина очистилась после покупки
        find( 0, null);
    }

    // Поиск продуктов в корзине с проверкой числа продуктов и подстроки total (опционально, null без проверки)
    private void find( Integer productCountExp, String totalSubstrExp) throws Exception {
        var res = wtc.get().uri( "/cart")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType( "text/html;charset=UTF-8")
            .expectBody()
            //.consumeWith( System.out::println) // вывод запроса и ответа
            .xpath( PRODUCTS_XPATH).nodeCount( productCountExp)
            .xpath( TOTAL_XPATH).nodeCount( 1)
        ;
        if( totalSubstrExp != null) {
            res.xpath(TOTAL_XPATH).string( Matchers.containsString( totalSubstrExp.toString()));
        }
    }

    // Проверка возможности покупки
    private void buyState( boolean enabled) throws Exception {
        wtc.get().uri( "/cart")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                // кнопка покупки есть и незаблокирована
                .xpath( "//*[@class=\"buyButton\"]").nodeCount( 1)
                .xpath( "//*[@class=\"buyButton\"][@disabled=\"disabled\"]").nodeCount( enabled ? 0 : 1)
                // нет сообщения о причине запрета покупки
                .xpath( "//*[@class=\"buyDisabledReason\"]").nodeCount( enabled ? 0 : 1)
        ;
    }

    private void changeQty( long productId, String action) throws Exception {
        wtc.mutateWith( csrf())
            .post().uri( "/cart/products/{productId}", productId)
            .contentType( MediaType.APPLICATION_FORM_URLENCODED)
            .body( BodyInserters.fromFormData( "action", action))
            .exchange()
            .expectStatus().isFound()
            .expectHeader().valueEquals( "Location", "/cart")
        ;
    }

}

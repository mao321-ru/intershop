package org.example.intershop.controller;

import org.assertj.core.api.AssertionsForClassTypes;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

public class ProductControllerTest extends ControllerTest {

    @Test
    void getProduct_noAuth() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        wtc.get().uri( "/products/{productId}", productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType( "text/html;charset=UTF-8")
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                // выводится хотя бы один товар
                .xpath( PRODUCTS_XPATH).nodeCount( 1)
                .xpath( PR_TEXT_XPF.formatted( "productName", EXISTS_PRODUCT_NAME))
                .nodeCount( 1)
                // у товара есть картинка
                .xpath( PR_SRC_XPF.formatted( "image", "/products/%d/image".formatted( productId)))
                .nodeCount( 1)
                // связанные с авторизацией ссылки
                .xpath( CART_LINK_XPATH).nodeCount( 0)
                .xpath( ORDERS_LINK_XPATH).nodeCount( 0)
                // присутствуют элементы для изменения количества товара в корзине
                .xpath( PRODUCT_IN_CART_XPATH).nodeCount( 0)
        ;
    }

    @Test
    void getProduct_notFound() throws Exception {
        final long productId = NOT_EXISTS_DATA_ID;
        wtc.get().uri( "/products/{productId}", productId)
                .exchange()
                .expectStatus().isNotFound()
        ;
    }

    @Test
    @WithMockUser( username = "user")
    void getProduct_auth() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        wtc.get().uri( "/products/{productId}", productId)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType( "text/html;charset=UTF-8")
            .expectBody()
            //.consumeWith( System.out::println) // вывод запроса и ответа
            // выводится один товар
            .xpath( PRODUCTS_XPATH).nodeCount( 1)
            .xpath( PR_TEXT_XPF.formatted( "productName", EXISTS_PRODUCT_NAME))
                .nodeCount( 1)
            // у товара есть картинка
            .xpath( PR_SRC_XPF.formatted( "image", "/products/%d/image".formatted( productId)))
                .nodeCount( 1)
            // связанные с авторизацией ссылки
            .xpath( CART_LINK_XPATH).nodeCount( 1)
            .xpath( ORDERS_LINK_XPATH).nodeCount( 1)
            // присутствуют элементы для изменения количества товара в корзине
            .xpath( PRODUCT_IN_CART_XPATH).nodeCount( 1)
        ;
    }

    @Test
    @WithMockUser( username = "user3")
    void getProduct_inCart() throws Exception {
        final long productId = USER3_IN_CART_PRODUCT_ID;
        wtc.get().uri( "/products/{productId}", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                // выводится один товар
                .xpath( PRODUCTS_XPATH).nodeCount( 1)
                // с указанным количеством в корзине
                .xpath( PR_TEXT_XPF.formatted( "inCartQuantity", USER3_IN_CART_PRODUCT_QTY)).nodeCount( 1)
        ;
    }

    @Test
    // у пользователя пустая корзина
    @WithMockUser( username = "user")
    void getProduct_emptyCart() throws Exception {
        // товар в корзине у другого пользователя
        final long productId = USER3_IN_CART_PRODUCT_ID;
        wtc.get().uri( "/products/{productId}", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                // выводится товар с количеством в корзине 0
                .xpath( PRODUCTS_XPATH).nodeCount( 1)
                .xpath( PR_TEXT_XPF.formatted( "inCartQuantity", 0)).nodeCount( 1)
        ;
    }

    @Test
    void getProduct_cache() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        String cachedName = EXISTS_PRODUCT_NAME;

        BiConsumer<String,String> nameCheck = ( name, step) -> {
            wtc.get().uri( "/products/{productId}", productId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .xpath( PR_FIELD_XPF.formatted( "productName"))
                    .string( s -> assertThat( s).as( step).isEqualTo( name))
            ;
        };

        nameCheck.accept( cachedName, "Initial value");

        // прямой update в БД: возвращаемое кэшированное значение не должно измениться
        assertThat( etm.getDatabaseClient()
                .sql( "update products set product_name = :productName where product_id = :productId")
                    .bind( "productName", cachedName + ": direct DB updated")
                    .bind( "productId", productId)
                    .fetch()
                    .rowsUpdated()
                    .block()
            )
            .as( "DB updated rows count")
            .isEqualTo( 1);
        nameCheck.accept( cachedName, "Use cached value after direct DB update");

    }

    @Test
    void getProductImage_check() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        wtc.get().uri( "/products/{productId}/image", productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType( "image/png")
        ;
    }

    @Test
    void getProductImage_notFound() throws Exception {
        final long productId = NOT_EXISTS_DATA_ID;
        wtc.get().uri( "/products/{productId}/image", productId)
                .exchange()
                .expectStatus().isNotFound()
        ;
    }

    @Test
    void changeInCartQuantity_noCsrf() throws Exception {
        wtc.post().uri( "/products/{productId}", EXISTS_PRODUCT_ID)
                .contentType( MediaType.APPLICATION_FORM_URLENCODED)
                .body( BodyInserters.fromFormData( "action", "plus"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().consumeWith( b -> AssertionsForClassTypes.assertThat( b.getResponseBody()).asString().contains( "CSRF token"))
        ;
    }

    @Test
    void changeInCartQuantity_noAuth() throws Exception {
        wtc.mutateWith( csrf())
            .post().uri( "/products/{productId}", EXISTS_PRODUCT_ID)
            .contentType( MediaType.APPLICATION_FORM_URLENCODED)
            .body( BodyInserters.fromFormData( "action", "plus"))
            .exchange()
            .expectStatus().isFound()
            .expectHeader().valueEquals( "Location", "/login" )
        ;
    }

    @Test
    @WithMockUser( username = "user")
    void changeInCartQuantity_check() throws Exception {
        long productId = EXISTS_PRODUCT_ID;
        Consumer<String> act = ( action) -> {
            wtc.mutateWith( csrf())
                    .post().uri( "/products/{productId}", productId)
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED)
                    .body( BodyInserters.fromFormData( "action", action))
                    .exchange()
                    .expectStatus().isFound()
                    .expectHeader().valueEquals( "Location", "/products/" + productId)
            ;
        };
        Consumer<Integer> check = ( qty) -> {
            wtc.get()
                    .uri( "/products/{productId}", productId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType( "text/html;charset=UTF-8")
                    .expectBody()
                    //.consumeWith( System.out::println) // вывод запроса и ответа
                    .xpath( PRODUCTS_XPATH).nodeCount( 1)
                    .xpath( PR_FIELD_XPF.formatted( "inCartQuantity"))
                    .nodeCount( 1)
                    .xpath( PR_TEXT_XPF.formatted( "inCartQuantity", qty))
                    .nodeCount( 1)
            ;
        };

        var pr = getProductById( productId);
        assertNotNull( "pre: Product not found", pr);
        var cp = getCartProductByProductId( productId);
        assertNull( "pre: Product already in cart", cp);

        act.accept( "plus");
        check.accept( 1);

        act.accept( "plus");
        check.accept( 2);

        act.accept( "minus");
        check.accept( 1);

        act.accept( "minus");
        check.accept( 0);

        cp = getCartProductByProductId( productId);
        assertNull( "CartProduct not deleted", cp);
    }

}

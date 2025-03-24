package org.example.intershop.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MainControllerTest extends ControllerTest {

    @Test
    void findProducts_startPageProductsExists() throws Exception {
        wtc.get().uri( "/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType( "text/html")
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                // выводится хотя бы один товар
                .xpath( PRODUCTS_XPATH).nodeCount( Matchers.greaterThan( 0))
        ;
    }

    @Test
    void findProducts_byNameOrDesc() throws Exception {
        final String search = "findProducts_byNameOrDesc_search";
        wtc.get()
                .uri( ub -> ub.path( "/")
                        .queryParam( "search", search)
                        .queryParam( "action", "")
                        .queryParam( "sort", "ALPHA")
                        .queryParam( "pageSize", "10")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                // должны найти товары в тестовых данных
                .xpath( PRODUCTS_XPATH).nodeCount( 2)
        ;
    }

    @Test
    void findProducts_nextPage() throws Exception {
        BiConsumer<List<Integer>,List<Integer>> act = (pageNumSize, expCountNext) -> {
            wtc.get()
                    .uri( ub -> ub.path( "/")
                            .queryParam( "pageNumber", pageNumSize.getFirst())
                            .queryParam( "pageSize", pageNumSize.getLast())
                            .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    // число товаров на странице
                    .xpath( PRODUCTS_XPATH).nodeCount( expCountNext.getFirst())
                    // возможность перехода на следующую страницу
                    .xpath( NEXT_PAGE_XPATH).nodeCount( expCountNext.getLast())
            ;
        };
        int pageSize = PRODUCTS_COUNT - 1;
        act.accept( List.of( 0, pageSize), List.of( pageSize, 1));
        act.accept( List.of( 1, pageSize), List.of( 1, 0));
        act.accept( List.of( 0, PRODUCTS_COUNT), List.of( PRODUCTS_COUNT, 0));
    }

    @Test
    void changeInCartQuantity_check() throws Exception {
        long productId = EXISTS_PRODUCT_ID;
        Consumer<String> act = ( action) -> {
            wtc.post().uri( "/main/products/{productId}", productId)
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED)
                    .body( BodyInserters
                        .fromFormData( "action", action)
                        .with("search", EXISTS_PRODUCT_NAME)
                        .with("sort", "ALPHA")
                        .with("pageSize", "10")
                        .with("pageNumber", "0")
                    )
                    .exchange()
                    .expectStatus().isFound()
                    .expectHeader().valueEquals(
                        "Location",
                        "/?search=%D0%A8%D0%B0%D0%BC%D0%BF%D1%83%D0%BD%D1%8C%20SUPER&sort=ALPHA&pageSize=10&pageNumber=0"
                    )
            ;
        };
        Consumer<Integer> check = ( qty) -> {
            wtc.get()
                    .uri( ub -> ub.path( "/")
                            .queryParam( "search", EXISTS_PRODUCT_NAME)
                            .queryParam( "sort", "ALPHA")
                            .queryParam( "pageSize", "10")
                            .queryParam( "pageNumber", "0")
                            .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType( "text/html")
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

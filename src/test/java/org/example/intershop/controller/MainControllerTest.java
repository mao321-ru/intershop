package org.example.intershop.controller;

//import org.example.intershop.model.CartProduct;
//import org.example.intershop.model.Product;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

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

//    @Test
//    void changeInCartQuantity_check() throws Exception {
//        long productId = EXISTS_PRODUCT_ID;
//        Consumer<String> act = ( action) -> {
//            try {
//                mockMvc.perform(post("/main/products/{productId}", productId)
//                                .param("action", action)
//                                .param("search", EXISTS_PRODUCT_NAME)
//                                .param("sort", "ALPHA")
//                                .param("pageSize", "10")
//                                .param("pageNumber", "0")
//                        )
//                        //.andDo( print()) // вывод запроса и ответа
//                        .andExpect(status().isFound())
//                        .andExpect(redirectedUrl("/?search=%3F%3F%3F%3F%3F%3F%3F+SUPER&sort=ALPHA&pageSize=10&pageNumber=0"))
//                ;
//            } catch (Exception e) {
//                throw new RuntimeException( e);
//            }
//        };
//        Consumer<Integer> check = ( qty) -> {
//            try {
//                mockMvc.perform( get( "/")
//                                .param("search", EXISTS_PRODUCT_NAME)
//                                .param("sort", "ALPHA")
//                                .param("pageSize", "10")
//                                .param("pageNumber", "0")
//                        )
//                        //.andDo( print()) // вывод запроса и ответа
//                        .andExpect( status().isOk())
//                        .andExpect( content().contentType( "text/html;charset=UTF-8"))
//                        .andExpect( xpath( PRODUCTS_XPATH).nodeCount( 1))
//                        .andExpect( xpath( PR_FIELD_XPF.formatted( "inCartQuantity"))
//                                .nodeCount( 1))
//                        .andExpect( xpath( PR_TEXT_XPF.formatted( "inCartQuantity", qty))
//                                .nodeCount( 1))
//                ;
//            } catch (Exception e) {
//                throw new RuntimeException( e);
//            }
//        };
//
//        var pr = em.find( Product.class, productId);
//        assertNotNull( "pre: Product not found", pr);
//        var cp = pr.getCartProduct();
//        assertNull( "pre: Product already in cart", cp);
//
//        act.accept( "plus");
//        check.accept( 1);
//
//        act.accept( "plus");
//        check.accept( 2);
//
//        act.accept( "minus");
//        check.accept( 1);
//
//        long cartProductId = em.find( Product.class, productId).getCartProduct().getId();
//        act.accept( "minus");
//        check.accept( 0);
//
//        cp = em.find( CartProduct.class, cartProductId);
//        assertNull( "CartProduct not deleted", cp);
//    }

}

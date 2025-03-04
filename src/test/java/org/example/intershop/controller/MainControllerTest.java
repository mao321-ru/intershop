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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MainControllerTest extends ControllerTest {

    @Autowired
    private ProductRepository repo;

    @Test
    void findProducts_startPageProductsExists() throws Exception {
        mockMvc.perform( get( "/"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // выводится хотя бы один товар
                .andExpect( xpath( PRODUCTS_XPATH).nodeCount( Matchers.greaterThan( 0)))
        ;
    }

    @Test
    void findProducts_byNameOrDesc() throws Exception {
        final String search = "findProducts_byNameOrDesc_search";
        mockMvc.perform( get( "/")
                        .param( "search", search)
                        .param( "action", "")
                        .param( "sort", "ALPHA")
                        .param( "pageSize", "10")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // должны найти товары в тестовых данных
                .andExpect( xpath( PRODUCTS_XPATH).nodeCount( 2))
        ;
    }

    @Test
    void changeInCartQuantity_check() throws Exception {
        long productId = EXISTS_PRODUCT_ID;
        Consumer<String> act = ( action) -> {
            try {
                mockMvc.perform(post("/main/products/{productId}", productId)
                                .param("action", action)
                                .param("search", EXISTS_PRODUCT_NAME)
                                .param("sort", "ALPHA")
                                .param("pageSize", "10")
                                .param("pageNumber", "0")
                        )
                        //.andDo( print()) // вывод запроса и ответа
                        .andExpect(status().isFound())
                        .andExpect(redirectedUrl("/?search=%3F%3F%3F%3F%3F%3F%3F+SUPER&sort=ALPHA&pageSize=10&pageNumber=0"))
                ;
            } catch (Exception e) {
                throw new RuntimeException( e);
            }
        };
        Consumer<Integer> check = ( qty) -> {
            try {
                mockMvc.perform( get( "/")
                                .param("search", EXISTS_PRODUCT_NAME)
                                .param("sort", "ALPHA")
                                .param("pageSize", "10")
                                .param("pageNumber", "0")
                        )
                        //.andDo( print()) // вывод запроса и ответа
                        .andExpect( status().isOk())
                        .andExpect( content().contentType( "text/html;charset=UTF-8"))
                        .andExpect( xpath( PRODUCTS_XPATH).nodeCount( 1))
                        .andExpect( xpath( PR_FIELD_XPF.formatted( "inCartQuantity"))
                                .nodeCount( qty > 0 ? 1 : 0))
                        .andExpect( xpath( PR_TEXT_XPF.formatted( "inCartQuantity", qty))
                                .nodeCount( qty > 0 ? 1 : 0))
                ;
            } catch (Exception e) {
                throw new RuntimeException( e);
            }
        };

        var pr = em.find( Product.class, productId);
        assertNotNull( "pre: Product not found", pr);
        var cp = pr.getCartProduct();
        assertNull( "pre: Product already in cart", cp);

        act.accept( "plus");
        check.accept( 1);

        act.accept( "plus");
        check.accept( 2);

        act.accept( "minus");
        check.accept( 1);

        long cartProductId = em.find( Product.class, productId).getCartProduct().getId();
        act.accept( "minus");
        check.accept( 0);

        cp = em.find( CartProduct.class, cartProductId);
        assertNull( "CartProduct not deleted", cp);
    }

}

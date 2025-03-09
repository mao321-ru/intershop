package org.example.intershop.controller;

import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Product;
import org.example.intershop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ProductControllerTest extends ControllerTest {

    @Autowired
    private ProductRepository repo;

    @Test
    void getProduct_check() throws Exception {
        mockMvc.perform( get( "/products/{productId}", EXISTS_PRODUCT_ID))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                .andExpect( xpath( PRODUCTS_XPATH).nodeCount( 1))
                .andExpect( xpath( PR_TEXT_XPF.formatted( "productName", EXISTS_PRODUCT_NAME))
                        .nodeCount( 1))
        ;
    }

    @Test
    void changeInCartQuantity_check() throws Exception {
        long productId = EXISTS_PRODUCT_ID;
        Consumer<String> act = ( action) -> {
            try {
                mockMvc.perform(post("/products/{productId}", productId)
                                .param("action", action)
                        )
                        //.andDo( print()) // вывод запроса и ответа
                        .andExpect(status().isFound())
                        .andExpect(redirectedUrl("/products/" + productId))
                ;
            } catch (Exception e) {
                throw new RuntimeException( e);
            }
        };
        Consumer<Integer> check = ( qty) -> {
            try {
                mockMvc.perform( get( "/products/{productId}", productId))
                        //.andDo( print()) // вывод запроса и ответа
                        .andExpect( status().isOk())
                        .andExpect( content().contentType( "text/html;charset=UTF-8"))
                        .andExpect( xpath( PRODUCTS_XPATH).nodeCount( 1))
                        .andExpect( xpath( PR_FIELD_XPF.formatted( "inCartQuantity"))
                                .nodeCount( 1))
                        .andExpect( xpath( PR_TEXT_XPF.formatted( "inCartQuantity", qty))
                                .nodeCount( 1))
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

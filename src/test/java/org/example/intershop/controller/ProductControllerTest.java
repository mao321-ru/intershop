package org.example.intershop.controller;

import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Product;
import org.example.intershop.repository.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ProductControllerTest extends ControllerTest {

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
        var pr = em.find( Product.class, productId);
        Consumer<String> act = ( action) -> {
            try {
                mockMvc.perform(post("/main/products/{productId}", productId)
                                .param("action", action)
                                .param("search", "")
                                .param("action", "")
                                .param("sort", "ALPHA")
                                .param("pageSize", "10")
                                .param("pageNumber", "0")
                        )
                        //.andDo( print()) // вывод запроса и ответа
                        .andExpect(status().isFound())
                        .andExpect(redirectedUrl("/?search=&sort=ALPHA&pageSize=10&pageNumber=0"))
                ;
            } catch (Exception e) {
                throw new RuntimeException( e);
            }
        };

        assertNull( "pre: Product already in cart", pr.getCartProduct());
        act.accept( "PLUS");
        pr = em.find( Product.class, productId);
        assertNotNull( "+1: Product not in cart", pr.getCartProduct());
        assertEquals( "+1: Unexpected quantity after add to cart", (long)pr.getCartProduct().getQuantity(), 1);

        // java.lang.StackOverflowError из-за зацикливания в toString для Product/CartProduct
        //assertNotEquals( "Test toString", pr.getCartProduct().toString().length(), 0);

        act.accept( "PLUS");
        pr = em.find( Product.class, productId);
        assertNotNull( "+2: Product not in cart", pr.getCartProduct());
        assertEquals( "+2: Unexpected quantity after add to cart", (long)pr.getCartProduct().getQuantity(), 2);

        act.accept( "MINUS");
        pr = em.find( Product.class, productId);
        assertNotNull( "+2-1: Product not in cart", pr.getCartProduct());
        assertEquals( "+2-1: Unexpected quantity after add to cart", (long)pr.getCartProduct().getQuantity(), 1);

        long cartProductId = pr.getCartProduct().getId();
        act.accept( "MINUS");
        pr = em.find( Product.class, productId);
        // java.lang.StackOverflowError из-за зацикливания в toString для Product/CartProduct
        //assertNull( "+2-2: Product in cart", pr.getCartProduct());
        assertTrue( "+2-2: Product in cart", pr.getCartProduct() == null);
        var cp = em.find( CartProduct.class, cartProductId);
        assertNull( "+2-2: CartProduct not deleted", cp);
    }
}

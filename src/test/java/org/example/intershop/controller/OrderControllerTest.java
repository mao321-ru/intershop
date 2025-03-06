package org.example.intershop.controller;

import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Product;
import org.example.intershop.repository.ProductRepository;
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
    void getOrder_check() throws Exception {
        mockMvc.perform( get( "/orders/{orderId}", EXISTS_PRODUCT_ID))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                .andExpect( xpath( PRODUCTS_XPATH).nodeCount( EXISTS_ORDER_PRODUCT_COUNT))
                .andExpect( xpath( TOTAL_TEXT_XPF.formatted( EXISTS_ORDER_TOTAL.toString()))
                        .nodeCount( 1))
        ;
    }

}

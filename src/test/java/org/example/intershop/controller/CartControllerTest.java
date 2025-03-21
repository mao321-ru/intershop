package org.example.intershop.controller;

import org.example.intershop.model.Order;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CartControllerTest extends ControllerTest {

    @Test
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
    void buy_check() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        final BigDecimal price = EXISTS_PRODUCT_PRICE;
        final long orderId = TEMP_DATA_START_ID;

        changeQty( productId, "plus");

        mockMvc.perform(post("/cart/buy", productId))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/orders/" + orderId + "?isNew=1"))
        ;

        Order order = em.find( Order.class, orderId);
        assertNotNull( "Order not found", order);
        assertEquals( "Unexpected order number", (Long) TEMP_DATA_START_ID, order.getNumber());
        assertEquals( "Unexpected products count", 1, order.getProducts().size());
        assertEquals( "Unexpected product_id", (Long) productId, order.getProducts().getFirst().getProduct().getId());
        assertEquals( "Unexpected amount", price, order.getProducts().getFirst().getAmount());

        find( 0, null);
    }

    // Поиск продуктов в корзине с проверкой числа продуктов и подстроки total (опционально, null без проверки)
    private void find( Integer productCountExp, String totalSubstrExp) throws Exception {
        var res = mockMvc.perform(get("/cart"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(xpath(PRODUCTS_XPATH).nodeCount( productCountExp))
                .andExpect(xpath(TOTAL_XPATH).nodeCount(1))
        ;
        if( totalSubstrExp != null) {
            res.andExpect(
                xpath(TOTAL_XPATH).string( Matchers.containsString( totalSubstrExp.toString()))
            );
        }
    }

    private void changeQty( long productId, String action) throws Exception {
        mockMvc.perform(post("/cart/products/{productId}", productId)
                        .param("action", action)
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/cart"))
        ;
    }

}

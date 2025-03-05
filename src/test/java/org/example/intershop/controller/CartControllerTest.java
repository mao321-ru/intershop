package org.example.intershop.controller;

import org.example.intershop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CartControllerTest extends ControllerTest {

    private final String TOTAL_XPATH = "//*[@class=\"total\"]";
    private final String TOTAL_TEXT_XPF = TOTAL_XPATH + "[text()=\"%s\"]";

    @Test
    void findCartProducts_check() throws Exception {
        final long productId = EXISTS_PRODUCT_ID;
        final String productName = EXISTS_PRODUCT_NAME;
        BigDecimal price = EXISTS_PRODUCT_PRICE;

        BiConsumer<Integer,String> find = ( cnt, total) -> {
            try {
                mockMvc.perform(get("/cart"))
                        //.andDo( print()) // вывод запроса и ответа
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("text/html;charset=UTF-8"))
                        .andExpect(xpath(PRODUCTS_XPATH).nodeCount(cnt))
                        .andExpect(xpath(TOTAL_XPATH).nodeCount(1))
                        .andExpect(xpath(TOTAL_TEXT_XPF.formatted(total)).nodeCount(1))
                ;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        BiConsumer<Long,String> change = ( prId, action) -> {
            try {
                mockMvc.perform(post("/cart/products/{productId}", prId)
                                .param("action", action)
                        )
                        //.andDo( print()) // вывод запроса и ответа
                        .andExpect(status().isFound())
                        .andExpect(redirectedUrl("/cart"))
                ;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        // изначально корзина пустая
        find.accept( 0, "0");

        // добавили товар в корзину
        change.accept( productId, "plus");
        find.accept( 1, price.toString());

        // увеличили до 2 шт
        change.accept( productId, "plus");
        find.accept( 1, price.multiply( BigDecimal.TWO).toString());
    }

}

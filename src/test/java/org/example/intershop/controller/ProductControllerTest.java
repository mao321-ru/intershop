package org.example.intershop.controller;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductControllerTest extends ControllerTest {

    // выбор всех элементов с товарами
    private final String PRODUCTS_XPATH = "//*[@class=\"product\"]";

    // выбор элемента с указанным значением поля товара, например FIELD_XPF.formatted( "productName", "Мыло DURU")
    private final String FIELD_XPF = PRODUCTS_XPATH + "//*[@class=\"product__%s\" and @value=\"%s\"]";

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
    void configProducts_productsExists() throws Exception {
        mockMvc.perform( get( "/config"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "text/html;charset=UTF-8"))
                // выводится хотя бы один товар
                .andExpect( xpath( PRODUCTS_XPATH).nodeCount( Matchers.greaterThan( 0)))
        ;
    }

    @Test
    void createProduct_NoImage() throws Exception {
        final String productName = "createProduct_NoImage";
        final String price = "904935.05";
        mockMvc.perform( multipart( "/config")
                        .file( new MockMultipartFile(
                            "file",
                            "",
                            "",
                            "".getBytes( StandardCharsets.UTF_8)
                        ))
                        .param( "productName", productName)
                        .param( "price", price)
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/config"))
        ;
        mockMvc.perform( get( "/config"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( xpath( FIELD_XPF.formatted( "productName", productName)).nodeCount( 1))
                .andExpect( xpath( FIELD_XPF.formatted( "price", price)).nodeCount( 1))
        ;
    }

}

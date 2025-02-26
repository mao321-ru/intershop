package org.example.intershop.controller;

import org.example.intershop.repository.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductControllerTest extends ControllerTest {

    // выбор всех элементов с товарами
    private final String PRODUCTS_XPATH = "//*[@class=\"product\"]";

    // выбор элемента для поля товара, например PR_FIELD_XPF.formatted( "productName")
    private final String PR_FIELD_XPF = PRODUCTS_XPATH + "//*[@class=\"product__%s\"]";

    // выбор элемента с указанным значением/текстом/ыsrc поля товара, например PR_VAL_XPF.formatted( "productName", "Мыло DURU")
    private final String PR_VAL_XPF = PR_FIELD_XPF + "[@value=\"%s\"]";
    private final String PR_TEXT_XPF = PR_FIELD_XPF + "[text()=\"%s\"]";
    private final String PR_SRC_XPF = PR_FIELD_XPF + "[@src=\"%s\"]";

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
                .andExpect( xpath( PR_VAL_XPF.formatted( "productName", productName)).nodeCount( 1))
                .andExpect( xpath( PR_VAL_XPF.formatted( "price", price)).nodeCount( 1))
        ;
    }

    @Test
    void createProduct_WithImage() throws Exception {
        final String productName = "createProduct_WithImage";
        final String price = "7384877.00";
        byte[] fileData = "image_data".getBytes( StandardCharsets.UTF_8);
        mockMvc.perform( multipart( "/config")
                        .file( new MockMultipartFile(
                                "file",
                                "createProduct_WithImage.png",
                                "image/png",
                                fileData
                        ))
                        .param( "productName", productName)
                        .param( "price", price)
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/config"))
        ;

        var pr = repo.findByName( productName);
        assertNotNull( "Product not found", pr);
        var productId = pr.getId();
        assertNotNull( "Product image not found", pr.getImage());
        var imgPath = "/products/%d/image".formatted( productId);

        // товар появился в настройках
        mockMvc.perform( get( "/config"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( xpath( PR_VAL_XPF.formatted( "productName", productName)).nodeCount( 1))
                .andExpect( xpath( PR_VAL_XPF.formatted( "price", price)).nodeCount( 1))
                // выводится изображение товара
                .andExpect( xpath(
            PR_VAL_XPF.formatted( "productName", productName)
                    + "/parent::*/preceding-sibling::*[child::img]"
                ).nodeCount( 1))
        ;

        // товар появился на главной странице
        mockMvc.perform( get( "/"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( xpath( PR_TEXT_XPF.formatted( "productName", productName)).nodeCount( 1))
                .andExpect( xpath( PR_TEXT_XPF.formatted( "price", price)).nodeCount( 1))
                .andExpect( xpath( PR_SRC_XPF.formatted( "image", imgPath)).nodeCount( 1))
        ;

        // возврат правильного изображения товара
        mockMvc.perform( get( "/products/{productId}/image", productId))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( content().contentType( "image/png"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Length", String.valueOf( fileData.length)))
                .andExpect( content().bytes( fileData))
        ;
    }

}

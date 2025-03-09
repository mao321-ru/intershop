package org.example.intershop.controller;

import jakarta.persistence.EntityManager;
import org.example.intershop.model.Image;
import org.example.intershop.model.Product;
import org.example.intershop.repository.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ConfigControllerTest extends ControllerTest {

    @Autowired
    private ProductRepository repo;

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
        final String description = "createProduct_NoImage desc";
        mockMvc.perform( multipart( "/config/products")
                        .file( new MockMultipartFile(
                            "file",
                            "",
                            "",
                            "".getBytes( StandardCharsets.UTF_8)
                        ))
                        .param( "productName", productName)
                        .param( "price", price)
                        .param( "description", description)
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
                .andExpect( xpath( PR_VAL_XPF.formatted( "description", description)).nodeCount( 1))
        ;
    }

    @Test
    void createProduct_WithImage() throws Exception {
        final String productName = "createProduct_WithImage";
        final String price = "7384877.00";
        final String description = "createProduct_WithImage desc";
        byte[] fileData = "image_data".getBytes( StandardCharsets.UTF_8);
        mockMvc.perform( multipart( "/config/products")
                        .file( new MockMultipartFile(
                                "file",
                                "createProduct_WithImage.png",
                                "image/png",
                                fileData
                        ))
                        .param( "productName", productName)
                        .param( "price", price)
                        .param( "description", description)
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/config"))
        ;

        var productId = TEMP_DATA_START_ID;
        var pr = em.find( Product.class, productId);
        assertNotNull( "Product not found", pr);
        assertNotNull( "Product image not found", pr.getImage());
        var imgPath = "/products/%d/image".formatted( productId);

        // товар появился в настройках
        mockMvc.perform( get( "/config"))
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isOk())
                .andExpect( xpath( PR_VAL_XPF.formatted( "productName", productName)).nodeCount( 1))
                .andExpect(
                    xpath( PR_VAL_XPF.formatted( "price", price.toString()))
                        .nodeCount( 1)
                )
                .andExpect( xpath( PR_VAL_XPF.formatted( "description", description)).nodeCount( 1))
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
                .andExpect( xpath( PR_TEXT_XPF.formatted( "price", price.toString() + " руб.")).nodeCount( 1))
                .andExpect( xpath( PR_TEXT_XPF.formatted( "description", description)).nodeCount( 1))
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

    @Test
    void updateProduct_check() throws Exception {
        var pr = getProductWithImage();
        var productId = pr.getId();
        var imageId = pr.getImage().getId();

        final String productName = "updateProduct_check";
        final String price = "90843.99";
        final String description = "updateProduct_check desc";
        final String origFilename = "updateProduct_check.jpg";
        final String contentType = "image/jpg";
        final byte[] fileData = "updateProduct_check_img".getBytes( StandardCharsets.UTF_8);

        mockMvc.perform( multipart( "/config/products/{productId}", productId)
                        .file(
                                new MockMultipartFile(
                                        "file",
                                        origFilename,
                                        contentType,
                                        fileData
                                )
                        )
                        .param( "_method", "")
                        .param( "productName", productName)
                        .param( "price", price)
                        .param( "description", description)
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/config" ))
        ;

        pr = em.find( Product.class, productId);
        assertNotNull( "Product not exists", pr);
        assertEquals( "Unexpected name", productName, pr.getName());
        assertEquals( "Unexpected price", price, pr.getPrice().toString());
        assertEquals( "Unexpected description", description, pr.getDescription());
        var img = pr.getImage();
        assertNotNull( "Product image not exists", img);
        // обход LazyInitializationException
        img = em.find( Image.class, img.getId());
        assertEquals( "Unexpected origFilename", origFilename, img.getOrigFilename());
        assertEquals( "Unexpected contentType", contentType, img.getContentType());
        assertTrue( "Unexpected fileData", Arrays.equals( fileData, img.getFileData()));
        assertEquals( "Unexpected imageId", imageId, img.getId());
    }

    @Test
    void updateProduct_setImage() throws Exception {
        var pr = getProductNoImage();
        var productId = pr.getId();

        final String productName = "updateProduct_setImage";
        final String price = "9348.01";
        final String description = "createProduct_setImage desc";
        final String origFilename = "updateProduct_setImage.jpg";
        final String contentType = "image/jpg";
        final byte[] fileData = "updateProduct_setImage_img".getBytes( StandardCharsets.UTF_8);

        mockMvc.perform( multipart( "/config/products/{productId}", productId)
                        .file(
                                new MockMultipartFile(
                                        "file",
                                        origFilename,
                                        contentType,
                                        fileData
                                )
                        )
                        .param( "_method", "")
                        .param( "productName", productName)
                        .param( "price", price)
                        .param( "description", description)
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/config" ))
        ;

        pr = em.find( Product.class, productId);
        assertNotNull( "Product not exists", pr);
        assertEquals( "Unexpected name", productName, pr.getName());
        assertEquals( "Unexpected price", price, pr.getPrice().toString());
        assertEquals( "Unexpected description", description, pr.getDescription());
        var img = pr.getImage();
        assertNotNull( "Product image not exists", img);
        // обход LazyInitializationException
        img = em.find( Image.class, img.getId());
        assertEquals( "Unexpected origFilename", origFilename, img.getOrigFilename());
        assertEquals( "Unexpected contentType", contentType, img.getContentType());
        assertTrue( "Unexpected fileData", Arrays.equals( fileData, img.getFileData()));
    }

    @Test
    void deleteProduct_check() throws Exception {
        var productId = UNSELLABLE_PRODUCT_ID;
        var pr = em.find( Product.class, productId);
        var imageId = pr.getImage().getId();

        mockMvc.perform( post( "/config/products/{productId}", productId)
                        .param( "_method", "delete")
                )
                //.andDo( print()) // вывод запроса и ответа
                .andExpect( status().isFound())
                .andExpect( redirectedUrl( "/config"));

        assertNull( "Product not deleted", em.find( Product.class, productId));
        assertNull( "Image not deleted", em.find( Image.class, imageId));
    }

    Product getProductWithImage() throws Exception {
        var productId = EXISTS_PRODUCT_ID;
        var pr = em.find( Product.class, productId);
        assertNotNull( "pre: Product not exists", pr);
        assertNotNull( "pre: Product image not exists", pr.getImage());
        var imageId = pr.getImage().getId();
        assertNotNull( "pre: Product imageId not exists", imageId);
        return pr;
    }

    Product getProductNoImage() throws Exception {
        var productId = NO_IMAGE_PRODUCT_ID;
        var pr = em.find( Product.class, productId);
        assertNotNull( "pre: Product not exists", pr);
        assertNull( "pre: Product image exists", pr.getImage());
        return pr;
    }

}

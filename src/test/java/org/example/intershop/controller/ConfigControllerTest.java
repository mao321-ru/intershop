package org.example.intershop.controller;

//import jakarta.persistence.EntityManager;
import org.example.intershop.model.Image;
import org.example.intershop.model.Product;
//import org.example.intershop.repository.ProductRepository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import static org.springframework.data.relational.core.query.Criteria.*;
import static org.springframework.data.relational.core.query.Query.query;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.FormPartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ConfigControllerTest extends ControllerTest {

    @Test
    void configProducts_productsExists() throws Exception {
        wtc.get().uri( "/config")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType( "text/html")
                .expectBody()
                    //.consumeWith( System.out::println) // вывод запроса и ответа
                    // выводится хотя бы один товар
                    .xpath( PRODUCTS_XPATH)
                        .nodeCount( Matchers.greaterThan( 0))
                    // у товара есть картинка
                    .xpath( PR_SRC_XPF.formatted( "image", "/products/%d/image".formatted( EXISTS_PRODUCT_ID)))
                        .nodeCount( 1)
        ;
    }

    @Test
    void createProduct_NoImage() throws Exception {
        final String productName = "createProduct_NoImage";
        final String price = "904935.05";
        final String description = "createProduct_NoImage desc";

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part( "productName", productName);
        builder.part( "price", price);
        builder.part( "description", description);
        builder.part( "file", new byte[]{})
                // правильные заголовки, которые передаются если изображение не выбрано
                .header( "Content-Disposition", "form-data; name=\"file\"; filename=\"\"")
                .contentType( MediaType.APPLICATION_OCTET_STREAM)
        ;
        // смотрим что как передается "file"
        //System.out.println( "\n\n* FILE *:\n" + builder.build().get( "file").toString());
        wtc.post().uri( "/config/products")
                .contentType( MediaType.MULTIPART_FORM_DATA)
                .body( BodyInserters.fromMultipartData( builder.build()))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals("Location", "/config")
        ;
        wtc.get().uri( "/config")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    //.consumeWith( System.out::println) // вывод запроса и ответа
                    .xpath( PR_VAL_XPF.formatted( "productName", productName)).nodeCount( 1)
                    .xpath( PR_VAL_XPF.formatted( "price", price)).nodeCount( 1)
                    .xpath( PR_VAL_XPF.formatted( "description", description)).nodeCount( 1)
        ;
    }

    @Test
    void createProduct_WithImage() throws Exception {
        final String productName = "createProduct_WithImage";
        final String price = "7384877.00";
        final String description = "createProduct_WithImage desc";
        final String filename = "createProduct_WithImage.png";
        byte[] fileData = "image_data".getBytes( StandardCharsets.UTF_8);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part( "productName", productName);
        builder.part( "price", price);
        builder.part( "description", description);
        builder.part( "file", fileData)
                // правильные заголовки, которые передаются если изображение не выбрано
                .header( "Content-Disposition",
                        "form-data; name=\"file\"; filename=\"%s\"".formatted( filename))
                .contentType( MediaType.IMAGE_PNG)
        ;
        //System.out.println( "\n\n* FILE *:\n" + builder.build().get( "file").toString());
        wtc.post().uri( "/config/products")
                .contentType( MediaType.MULTIPART_FORM_DATA)
                .body( BodyInserters.fromMultipartData( builder.build()))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals("Location", "/config")
        ;

        var productId = TEMP_DATA_START_ID;
        var pr = getProductById( productId);

        assertNotNull( "Product not found", pr);
        assertNotNull( "Product image_id not found", pr.getImageId());
        var imgPath = "/products/%d/image".formatted( productId);

        // товар появился в настройках
        wtc.get().uri( "/config")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                .xpath( PR_VAL_XPF.formatted( "productName", productName)).nodeCount( 1)
                .xpath( PR_VAL_XPF.formatted( "price", price)).nodeCount( 1)
                .xpath( PR_VAL_XPF.formatted( "description", description)).nodeCount( 1)
                // выводится изображение товара
                .xpath(
                    PR_VAL_XPF.formatted( "productName", productName)
                        + "/parent::*/preceding-sibling::*[child::img]"
                    ).nodeCount( 1)
        ;

//        // товар появился на главной странице
//        mockMvc.perform( get( "/"))
//                //.andDo( print()) // вывод запроса и ответа
//                .andExpect( status().isOk())
//                .andExpect( xpath( PR_TEXT_XPF.formatted( "productName", productName)).nodeCount( 1))
//                .andExpect( xpath( PR_TEXT_XPF.formatted( "price", price.toString() + " руб.")).nodeCount( 1))
//                .andExpect( xpath( PR_TEXT_XPF.formatted( "description", description)).nodeCount( 1))
//                .andExpect( xpath( PR_SRC_XPF.formatted( "image", imgPath)).nodeCount( 1))
//        ;

        // возврат правильного изображения товара
        wtc.get().uri( "/products/{productId}/image", productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType( "image/png")
                .expectHeader().contentLength( fileData.length)
                .expectBody( String.class).isEqualTo( new String( fileData, "UTF-8"))
        ;
    }

//    @Test
//    void updateProduct_check() throws Exception {
//        var pr = getProductWithImage();
//        var productId = pr.getId();
//        var imageId = pr.getImage().getId();
//
//        final String productName = "updateProduct_check";
//        final String price = "90843.99";
//        final String description = "updateProduct_check desc";
//        final String origFilename = "updateProduct_check.jpg";
//        final String contentType = "image/jpg";
//        final byte[] fileData = "updateProduct_check_img".getBytes( StandardCharsets.UTF_8);
//
//        mockMvc.perform( multipart( "/config/products/{productId}", productId)
//                        .file(
//                                new MockMultipartFile(
//                                        "file",
//                                        origFilename,
//                                        contentType,
//                                        fileData
//                                )
//                        )
//                        .param( "_method", "")
//                        .param( "productName", productName)
//                        .param( "price", price)
//                        .param( "description", description)
//                )
//                //.andDo( print()) // вывод запроса и ответа
//                .andExpect( status().isFound())
//                .andExpect( redirectedUrl( "/config" ))
//        ;
//
//        pr = em.find( Product.class, productId);
//        assertNotNull( "Product not exists", pr);
//        assertEquals( "Unexpected name", productName, pr.getName());
//        assertEquals( "Unexpected price", price, pr.getPrice().toString());
//        assertEquals( "Unexpected description", description, pr.getDescription());
//        var img = pr.getImage();
//        assertNotNull( "Product image not exists", img);
//        // обход LazyInitializationException
//        img = em.find( Image.class, img.getId());
//        assertEquals( "Unexpected origFilename", origFilename, img.getOrigFilename());
//        assertEquals( "Unexpected contentType", contentType, img.getContentType());
//        assertTrue( "Unexpected fileData", Arrays.equals( fileData, img.getFileData()));
//        assertEquals( "Unexpected imageId", imageId, img.getId());
//    }
//
//    @Test
//    void updateProduct_setImage() throws Exception {
//        var pr = getProductNoImage();
//        var productId = pr.getId();
//
//        final String productName = "updateProduct_setImage";
//        final String price = "9348.01";
//        final String description = "createProduct_setImage desc";
//        final String origFilename = "updateProduct_setImage.jpg";
//        final String contentType = "image/jpg";
//        final byte[] fileData = "updateProduct_setImage_img".getBytes( StandardCharsets.UTF_8);
//
//        mockMvc.perform( multipart( "/config/products/{productId}", productId)
//                        .file(
//                                new MockMultipartFile(
//                                        "file",
//                                        origFilename,
//                                        contentType,
//                                        fileData
//                                )
//                        )
//                        .param( "_method", "")
//                        .param( "productName", productName)
//                        .param( "price", price)
//                        .param( "description", description)
//                )
//                //.andDo( print()) // вывод запроса и ответа
//                .andExpect( status().isFound())
//                .andExpect( redirectedUrl( "/config" ))
//        ;
//
//        pr = em.find( Product.class, productId);
//        assertNotNull( "Product not exists", pr);
//        assertEquals( "Unexpected name", productName, pr.getName());
//        assertEquals( "Unexpected price", price, pr.getPrice().toString());
//        assertEquals( "Unexpected description", description, pr.getDescription());
//        var img = pr.getImage();
//        assertNotNull( "Product image not exists", img);
//        // обход LazyInitializationException
//        img = em.find( Image.class, img.getId());
//        assertEquals( "Unexpected origFilename", origFilename, img.getOrigFilename());
//        assertEquals( "Unexpected contentType", contentType, img.getContentType());
//        assertTrue( "Unexpected fileData", Arrays.equals( fileData, img.getFileData()));
//    }
//
    @Test
    void deleteProduct_check() throws Exception {
        var productId = UNSELLABLE_PRODUCT_ID;
        var pr = getProductById( productId);
        var imageId = pr.getImageId();

        wtc.post().uri( "/config/products/{productId}", productId)
                .body( BodyInserters.fromMultipartData( "_method", "delete"))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals("Location", "/config")
        ;

        assertNull( "Product not deleted", getProductById( productId));
        assertNull( "Image not deleted", getImageById( imageId));
    }

    @Test
    void deleteProduct_notFound() throws Exception {
        var productId = NOT_EXISTS_DATA_ID;

        wtc.post().uri( "/config/products/{productId}", productId)
                .body( BodyInserters.fromMultipartData( "_method", "delete"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody( String.class).isEqualTo( null)
        ;
    }

//    Product getProductWithImage() throws Exception {
//        var productId = EXISTS_PRODUCT_ID;
//        var pr = em.find( Product.class, productId);
//        assertNotNull( "pre: Product not exists", pr);
//        assertNotNull( "pre: Product image not exists", pr.getImage());
//        var imageId = pr.getImage().getId();
//        assertNotNull( "pre: Product imageId not exists", imageId);
//        return pr;
//    }
//
//    Product getProductNoImage() throws Exception {
//        var productId = NO_IMAGE_PRODUCT_ID;
//        var pr = em.find( Product.class, productId);
//        assertNotNull( "pre: Product not exists", pr);
//        assertNull( "pre: Product image exists", pr.getImage());
//        return pr;
//    }

}

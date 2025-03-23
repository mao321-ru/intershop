package org.example.intershop.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;

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

        wtc.post().uri( "/config/products")
                .contentType( MediaType.MULTIPART_FORM_DATA)
                .body( makeMultipartBody( productName, price, description))
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

        wtc.post().uri( "/config/products")
                .contentType( MediaType.MULTIPART_FORM_DATA)
                .body( makeMultipartBody( productName, price, description, filename, fileData))
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

        // возврат правильного изображения товара
        wtc.get().uri( "/products/{productId}/image", productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType( "image/png")
                .expectHeader().contentLength( fileData.length)
                .expectBody( String.class).isEqualTo( new String( fileData, "UTF-8"))
        ;

        // товар появился на витрине
        wtc.get().uri( "/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.consumeWith( System.out::println) // вывод запроса и ответа
                .xpath( PR_TEXT_XPF.formatted( "productName", productName)).nodeCount( 1)
                .xpath( PR_TEXT_XPF.formatted( "price", price + " руб.")).nodeCount( 1)
                .xpath( PR_TEXT_XPF.formatted( "description", description)).nodeCount( 1)
                .xpath( PR_SRC_XPF.formatted( "image", imgPath)).nodeCount( 1)
        ;
    }

    @Test
    void createProduct_checkRollback() throws Exception {
        final String productName = "createProduct_checkRollback";
        // обеспечиваем ошибку при сохранении товара в БД (выполняется после сохранения изображения)
        final String price = "";
        final String description = "createProduct_checkRollback desc";
        final String filename = "createProduct_checkRollback.png";
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
                .expectStatus().is5xxServerError()
        ;

        assertNull( "Product found", getProductById( TEMP_DATA_START_ID));
        assertNull( "Image_id found", getImageById( TEMP_DATA_START_ID));
    }

    @Test
    void updateProduct_check() throws Exception {
        var pr = getProductById( EXISTS_PRODUCT_ID);
        var productId = pr.getId();
        var imageId = pr.getImageId();

        final String productName = "updateProduct_check";
        final String price = "90843.99";
        final String description = "updateProduct_check desc";
        final String filename = "updateProduct_check.jpg";
        final String contentType = MediaType.IMAGE_JPEG.toString();
        final byte[] fileData = "updateProduct_check_img".getBytes( StandardCharsets.UTF_8);

        wtc.post().uri( "/config/products/{productId}", productId)
                .contentType( MediaType.MULTIPART_FORM_DATA)
                .body( makeMultipartBody( productName, price, description, filename, fileData))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals("Location", "/config")
        ;

        pr = getProductById( productId);
        assertNotNull( "Product not exists", pr);
        assertEquals( "Unexpected name", productName, pr.getName());
        assertEquals( "Unexpected price", price, pr.getPrice().toString());
        assertEquals( "Unexpected description", description, pr.getDescription());

        var newImageId = pr.getImageId();
        assertNotNull( "Product image not exists", newImageId);
        // обход LazyInitializationException
        var img = getImageById( newImageId);
        assertEquals( "Unexpected filename", filename, img.getOrigFilename());
        assertEquals( "Unexpected contentType", contentType, img.getContentType());
        assertTrue( "Unexpected fileData", Arrays.equals( fileData, img.getFileData()));
        assertEquals( "Unexpected imageId", imageId, img.getId());
    }

    @Test
    void updateProduct_setImage() throws Exception {
        var productId = NO_IMAGE_PRODUCT_ID;

        final String productName = "updateProduct_setImage";
        final String price = "9348.01";
        final String description = "updateProduct_setImage desc";
        final String filename = "updateProduct_setImage.jpg";
        final String contentType = MediaType.IMAGE_JPEG.toString();
        final byte[] fileData = "updateProduct_setImage_img".getBytes( StandardCharsets.UTF_8);

        wtc.post().uri( "/config/products/{productId}", productId)
                .contentType( MediaType.MULTIPART_FORM_DATA)
                .body( makeMultipartBody( productName, price, description, filename, fileData))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals("Location", "/config")
        ;

        var pr = getProductById( productId);
        assertNotNull( "Product not exists", pr);
        assertEquals( "Unexpected name", productName, pr.getName());
        assertEquals( "Unexpected price", price, pr.getPrice().toString());
        assertEquals( "Unexpected description", description, pr.getDescription());

        var newImageId = pr.getImageId();
        assertNotNull( "Product image not exists", newImageId);
        // обход LazyInitializationException
        var img = getImageById( newImageId);
        assertEquals( "Unexpected filename", filename, img.getOrigFilename());
        assertEquals( "Unexpected contentType", contentType, img.getContentType());
        assertTrue( "Unexpected fileData", Arrays.equals( fileData, img.getFileData()));
    }


    @Test
    void updateProduct_delImage() throws Exception {
        var productId = EXISTS_PRODUCT_ID;
        var pr = getProductById( productId);
        var imageId = pr.getImageId();

        final String productName = "updateProduct_delImage";
        final String price = "9348.01";
        final String description = "updateProduct_delImage desc";
        final String filename = "updateProduct_delImage.jpg";
        final String contentType = MediaType.IMAGE_JPEG.toString();
        final byte[] fileData = "updateProduct_delImage_img".getBytes( StandardCharsets.UTF_8);

        wtc.post().uri( "/config/products/{productId}", productId)
                .contentType( MediaType.MULTIPART_FORM_DATA)
                .body( makeMultipartBody( productName, price, description, filename, fileData, true))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals("Location", "/config")
        ;

        pr = getProductById( productId);
        assertNotNull( "Product not exists", pr);
        assertEquals( "Unexpected name", productName, pr.getName());
        assertEquals( "Unexpected price", price, pr.getPrice().toString());
        assertEquals( "Unexpected description", description, pr.getDescription());

        assertNull( "Product image exists", pr.getImageId());
        assertNull( "Image not deleted", getImageById( imageId));
    }

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


    // тело запроса без передачи картинки
    private BodyInserters.MultipartInserter makeMultipartBody(
            String productName,
            String price,
            String description
    )
    {
        return makeMultipartBody(
                productName,
                price,
                description,
                "",
                new byte[]{},
                false
        );
    }

    // тело запроса c картинкой
    private BodyInserters.MultipartInserter makeMultipartBody(
            String productName,
            String price,
            String description,
            String filename,
            byte[] fileData
    )
    {
        return makeMultipartBody(
                productName,
                price,
                description,
                filename,
                fileData,
                false
        );
    }

    // тело запроса c картинкой и опциональным удалением картинки (при delImage == true)
    private BodyInserters.MultipartInserter makeMultipartBody(
            String productName,
            String price,
            String description,
            String filename,
            byte[] fileData,
            boolean delImage
    )
    {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part( "productName", productName);
        builder.part( "price", price);
        builder.part( "description", description);
        builder.part( "file", fileData)
                // правильные заголовки, которые передаются если изображение выбрано
                .header( "Content-Disposition",
                        "form-data; name=\"file\"; filename=\"%s\"".formatted( filename))
                .contentType(
                        filename.endsWith( ".jpg") ? MediaType.IMAGE_JPEG :
                                filename.endsWith( ".png") ? MediaType.IMAGE_PNG
                                        : MediaType.APPLICATION_OCTET_STREAM
                )
        ;
        if( delImage) builder.part( "delImage", "on");
        //System.out.println( "\n\n* FILE *:\n" + builder.build().get( "file").toString());
        return BodyInserters.fromMultipartData( builder.build());
    }

}

package org.example.intershop.mapper;

import lombok.SneakyThrows;
import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.model.Product;
import org.example.intershop.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class ProductMapper {

    public static ProductDto toProductDto( Product p) {
        return ProductDto.builder()
                .productId( p.getId())
                .productName( p.getName())
                .price( p.getPrice())
                .description( p.getDescription())
                .isImage( p.getImage() != null)
                .inCartQuantity( p.getCartProduct() != null ? p.getCartProduct().getQuantity() : 0)
                .build();
    }

    @SneakyThrows
    public static Product toProduct( ProductCreateDto dto) {
        MultipartFile f = dto.getFile();
        String priceStr = dto.getPrice();
        return Product.builder()
                .name( dto.getProductName())
                .price( priceStr.isEmpty() ? null : new BigDecimal( priceStr))
                .description( dto.getDescription())
                .image(
                        f == null || f.isEmpty()
                                ? null
                                : Image.builder()
                                        .origFilename( f.getOriginalFilename())
                                        .contentType( f.getContentType())
                                        .fileData( f.getBytes())
                                        .build()
                )
                .build();
    }

    @SneakyThrows
    public static void changeProduct( Product pr, ProductUpdateDto dto) {
        String s;
        if( ( s = dto.getProductName()) != null && ! s.isBlank()) pr.setName( s.trim());
        if( ( s = dto.getPrice()) != null && ! s.isBlank()) pr.setPrice( new BigDecimal( s.trim()));
        pr.setDescription( dto.getDescription());
        if ( dto.getDelImage() != null && dto.getDelImage()) {
            pr.setImage( null);
        }
        else {
            MultipartFile f = dto.getFile();
            if ( f != null && ! f.isEmpty()) {
                Image img = pr.getImage();
                if ( img == null) {
                    img = new Image();
                    pr.setImage( img);
                }
                img.setOrigFilename( f.getOriginalFilename());
                img.setContentType( f.getContentType());
                img.setFileData( f.getBytes());
            }
        }
    }

}

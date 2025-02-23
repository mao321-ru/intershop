package org.example.intershop.mapper;

import lombok.SneakyThrows;
import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.model.Product;
import org.example.intershop.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class ProductMapper {

    public static ProductDto toProductDto( Product p) {
        return ProductDto.builder()
                .productId( p.getProductId())
                .productName( p.getProductName())
                .price( p.getPrice())
                .isImage( p.getImage() != null)
                .inCartQuantity( 0)
                .build();
    }

    @SneakyThrows
    public static Product toProduct( ProductCreateDto dto) {
        MultipartFile f = dto.getFile();
        String priceStr = dto.getPrice();
        return Product.builder()
                .productName( dto.getProductName())
                .price( priceStr.isEmpty() ? null : new BigDecimal( priceStr))
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
}

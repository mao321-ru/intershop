package org.example.intershop.mapper;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.model.Product;
import org.example.intershop.model.Image;

import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.math.BigDecimal;

public class ProductMapper {

    public static ProductDto toProductDto( Product p) {
        return ProductDto.builder()
                .productId( p.getId())
                .productName( p.getName())
                .price( p.getPrice())
                .description( p.getDescription())
                .isImage( p.getImageId() != null)
                .inCartQuantity( p.getInCartQuantity() == null ? 0 : p.getInCartQuantity())
                .build();
    }

    @SneakyThrows
    public static Product toProduct( ProductCreateDto dto) {
        String priceStr = dto.getPrice();
        return Product.builder()
                .name( dto.getProductName())
                .price( priceStr.isEmpty() ? null : new BigDecimal( priceStr))
                .description( dto.getDescription())
                .build();
    }

    @SneakyThrows
    public static void changeProduct( Product pr, ProductUpdateDto dto) {
        String s;
        if( ( s = dto.getProductName()) != null && ! s.isBlank()) pr.setName( s.trim());
        if( ( s = dto.getPrice()) != null && ! s.isBlank()) pr.setPrice( new BigDecimal( s.trim()));
        pr.setDescription( dto.getDescription());
    }

}

package org.example.intershop.mapper;

import org.example.intershop.dto.ProductDto;
import org.example.intershop.model.Product;

public class ProductMapper {

    public static ProductDto toProductDto( Product p) {
        return ProductDto.builder()
                .productId( p.getProductId())
                .productName( p.getProductName())
                .price( p.getPrice())
                .isImage( false)
                .inCartQuantity( 0)
                .build();
    }
}

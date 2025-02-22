package org.example.intershop.mapper;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.model.Product;

import java.math.BigDecimal;

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

    public static Product toProduct(ProductCreateDto dto) {
        return Product.builder()
                .productName( dto.getProductName())
                .price( new BigDecimal( dto.getPrice()))
                .build();
    }
}

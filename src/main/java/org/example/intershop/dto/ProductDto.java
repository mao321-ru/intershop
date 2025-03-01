package org.example.intershop.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ProductDto {
    private final long productId;
    private final String productName;
    private final BigDecimal price;
    private final String description;
    private final boolean isImage;
    private final int inCartQuantity;
}

package org.example.intershop.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ProductDto {
    private long productId;
    private String productName;
    private BigDecimal price;
    private String description;
    private boolean isImage;
    private int inCartQuantity;
}

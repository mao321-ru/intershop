package org.example.intershop.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OrderProductDto {
    private final long productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal amount;
    private final boolean isImage;
}

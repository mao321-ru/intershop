package org.example.intershop.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class OrderDto {
    private final List<OrderProductDto> products;
    private final long orderNumber;
    private final BigDecimal total;
}

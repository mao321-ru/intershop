package org.example.intershop.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Table( name = "order_products")
@NoArgsConstructor
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class OrderProduct {

    @Id
    @Column( "order_product_id")
    private Long id;

    private Long orderId;

    private Long productId;

    private Integer quantity;

    private BigDecimal amount;

    // вычисляемые колонки (из products)
    private String productName;
    private Long imageId;

    @Override
    public boolean equals( Object o) {
        if( this == o) return true;
        if( o == null || getClass() != o.getClass()) return false;
        OrderProduct other = (OrderProduct) o;
        return id != null && Objects.equals( id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash( id);
    }
}

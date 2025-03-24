package org.example.intershop.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table( name = "cart_products")
@NoArgsConstructor
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CartProduct {

    @Id
    @Column( "cart_product_id")
    private Long id;

    private Integer quantity;

    private Long productId;

    @Override
    public boolean equals( Object o) {
        if( this == o) return true;
        if( o == null || getClass() != o.getClass()) return false;
        CartProduct other = (CartProduct) o;
        return id != null && Objects.equals( id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash( id);
    }
}

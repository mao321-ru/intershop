package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
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
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "cart_product_id")
    private Long id;

    private Integer quantity;

    @OneToOne
    @JoinColumn( name = "product_id", referencedColumnName = "product_id", unique = true)
    @ToString.Exclude
    private Product product;

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

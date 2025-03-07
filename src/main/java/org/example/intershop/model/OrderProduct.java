package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table( name = "order_products", uniqueConstraints = { @UniqueConstraint( columnNames = { "order_id", "product_id"})})
@NoArgsConstructor
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class OrderProduct {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "order_product_id")
    private Long id;

    @Column( nullable = false)
    private Integer quantity;

    @Column( nullable = false)
    private BigDecimal amount;

    @OneToOne( fetch = FetchType.LAZY)
    @JoinColumn( name = "order_id", referencedColumnName = "order_id", nullable = false, updatable = false)
    @ToString.Exclude
    private Order order;

    @OneToOne( fetch = FetchType.EAGER)
    @JoinColumn( name = "product_id", referencedColumnName = "product_id", nullable = false, updatable = false)
    @ToString.Exclude
    private Product product;

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

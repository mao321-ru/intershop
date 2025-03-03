package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table( name = "products")
@NoArgsConstructor
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "product_id")
    private Long id;

    @Column( name = "product_name", nullable = false)
    private String name;

    private BigDecimal price;

    private String description;

    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn( name = "image_id", referencedColumnName = "image_id")
    @ToString.Exclude
    private Image image;

    @OneToOne( mappedBy = "product")
    @ToString.Exclude
    private CartProduct cartProduct;

    @Override
    public boolean equals( Object o) {
        if( this == o) return true;
        if( o == null || getClass() != o.getClass()) return false;
        Product other = (Product) o;
        return id != null && Objects.equals( id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash( id);
    }
}

package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table( name = "cart_products")
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CartProduct {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "cart_product_id")
    private Long id;

    private Integer quantity;

    @OneToOne
    @JoinColumn( name = "product_id", referencedColumnName = "product_id", unique = true)
    private Product product;
}

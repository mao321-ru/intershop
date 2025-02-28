package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table( name = "products")
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "product_id")
    private Long id;

    @Column( name = "product_name", nullable = false)
    private String name;

    private BigDecimal price;

    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@OneToOne( cascade = { CascadeType.DETACH, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn( name = "image_id", referencedColumnName = "image_id")
    private Image image;
}

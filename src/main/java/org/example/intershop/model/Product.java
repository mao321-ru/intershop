package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.intershop.dto.ProductDto;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table( name = "products")
public class Product {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "product_id")
    private long productId;

    @Column( name = "product_name", nullable = false)
    private String productName;

    private BigDecimal price;

}

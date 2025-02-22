package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.intershop.dto.ProductDto;

import java.math.BigDecimal;

@Entity
@Table( name = "products")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "product_id")
    private long productId;

    @Column( name = "product_name", nullable = false)
    private String productName;

    private BigDecimal price;

}

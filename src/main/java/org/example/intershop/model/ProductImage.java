package org.example.intershop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "product_images")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ProductImage {
    @Id
    @Column( name = "product_id", nullable = false, unique = true)
    long productId;

    @Column( name = "orig_filename", nullable = false)
    String origFilename;

    @Column( name = "content_type", nullable = false)
    String contentType;

    @Column( name = "file_data", nullable = false)
    byte[] fileData;
}

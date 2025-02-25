package org.example.intershop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "images")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "image_id")
    Long id;

    @Column( name = "orig_filename", nullable = false)
    String origFilename;

    @Column( name = "content_type", nullable = false)
    String contentType;

    @Column( name = "file_data", nullable = false)
    byte[] fileData;
}

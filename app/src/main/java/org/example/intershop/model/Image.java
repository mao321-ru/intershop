package org.example.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table( name = "images")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Image {
    @Id
    @Column(  "image_id")
    Long id;
    String origFilename;
    String contentType;
    byte[] fileData;
}

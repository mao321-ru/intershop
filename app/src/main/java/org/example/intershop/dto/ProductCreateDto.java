package org.example.intershop.dto;

import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

@Data
public class ProductCreateDto {
    private final String productName;
    private final String price;
    private final String description;
    private final FilePart file;
}


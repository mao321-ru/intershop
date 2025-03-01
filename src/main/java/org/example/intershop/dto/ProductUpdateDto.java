package org.example.intershop.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductUpdateDto {
    private final Long productId;
    private final String productName;
    private final String price;
    private final String description;
    private final MultipartFile file;
    private final Boolean delImage;
}


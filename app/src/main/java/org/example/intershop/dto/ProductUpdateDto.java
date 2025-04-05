package org.example.intershop.dto;

import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.BindParam;

@Data
public class ProductUpdateDto {
    private final Long productId;
    private final String productName;
    private final String price;
    private final String description;
    private final FilePart file;
    private final Boolean delImage;

    @BindParam( "_method")
    private final String method;
}


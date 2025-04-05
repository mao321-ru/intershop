package org.example.intershop.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
// @AllArgsConstructor требуется для @Builder после добавления @NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SliceProductDto {
    private List<ProductDto> content;
    private int size;
    private int number;
    boolean isNext;

    public boolean hasPrevious() { return number > 0; }

    public boolean hasNext() { return isNext; }
}

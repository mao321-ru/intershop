package org.example.intershop.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum ProductSort {

    NO( Sort.unsorted()),
    ALPHA( Sort.by( Sort.Direction.ASC, "name")),
    PRICE( Sort.by( Sort.Direction.ASC, "price"));

    private final Sort sortValue;
}

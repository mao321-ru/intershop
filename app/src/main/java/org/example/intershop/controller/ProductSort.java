package org.example.intershop.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum ProductSort {

    NO( Sort.unsorted()),
    // в свойстве указывается имя колонки БД (а не имя поля в Java) в связи с ручным формированием SQL
    ALPHA( Sort.by( Sort.Direction.ASC, "product_name")),
    PRICE( Sort.by( Sort.Direction.ASC, "price"));

    private final Sort sortValue;
}

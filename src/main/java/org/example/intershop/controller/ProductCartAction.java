package org.example.intershop.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum ProductCartAction {
    DELETE( null),
    MINUS( -1),
    PLUS( 1);

    private final Integer delta;
}

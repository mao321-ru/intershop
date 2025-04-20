package org.example.intershop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Более информационное сообщение об ошибке, т.ч. для ошибок платежного сервиса
    @ExceptionHandler( RuntimeException.class)
    public Mono<ResponseEntity<String>> handleException(RuntimeException e) {
        String msg = e.getCause() != null
            ? "%s: %s".formatted( e.getMessage(), e.getCause().getMessage())
            : e.getMessage();
        log.error( "HTTP 500 response: %s".formatted( msg));
        log.error( "{}", e);
        return Mono.just( ResponseEntity.internalServerError().body( msg));
    }

}

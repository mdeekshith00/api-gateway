package com.api_gateway.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Object>> handleAllExceptions(
            ServerWebExchange exchange,
            Exception ex
    ) {
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", ex.getMessage(),
                    "path", exchange.getRequest().getPath().value()
                ))
        );
    }
}

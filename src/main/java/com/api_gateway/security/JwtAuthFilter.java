package com.api_gateway.security;

import java.util.List;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.common.security.JWTService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JWTService jwtService;

    private static final List<String> PUBLIC = List.of(
            "/user/sign-up",
            "/user/sign-in",
            "/user/refresh-token",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (PUBLIC.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return this.unauthorized(exchange, "Missing token");
        }

        String token = auth.substring(7);

        try {
            var claims = jwtService.extractAllClaims(token);

            ServerWebExchange updated = exchange.mutate()
                    .request(builder -> builder
                            .headers(headers -> {
                                headers.add("X-USER-ID", claims.getSubject());
                                headers.add("X-ROLES", claims.get("roles").toString());
                            })
                    ).build();

            return chain.filter(updated);

        } catch (Exception e) {
            return this.unauthorized(exchange, "Invalid or expired token");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
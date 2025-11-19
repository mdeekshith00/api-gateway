//package com.api_gateway.security;
//
//import java.util.List;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//
//import com.common.security.JWTService;
//
//import io.jsonwebtoken.Claims;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Mono;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class JWTFilter implements GlobalFilter, Ordered {
//
//    private final JWTService jwtService;
//
//    private static final List<String> PUBLIC_PATHS = List.of(
//            "/user/sign-up",
//            "/user/sign-in",
//            "/user/refresh-token",
//            "/v3/api-docs",
//            "/swagger-ui",
//            "/swagger-ui.html"
//    );
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        String path = exchange.getRequest().getURI().getPath();
//        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
//            return chain.filter(exchange);
//        }
//
//        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return unauthorized(exchange, "Missing token");
//        }
//
//        String token = authHeader.substring(7);
//
//        try {
//            if (!jwtService.validateToken(token)) {
//                return unauthorized(exchange, "Invalid/Expired token");
//            }
//
//            Claims claims = jwtService.extractAllClaims(token);
//            String userId = claims.getSubject();
//            String roles = claims.get("roles", String.class);
//
//            log.debug("✅ Valid token → userId={}, roles={}", userId, roles);
//
//            // forward headers
//            ServerHttpRequest mutatedRequest =
//                    exchange.getRequest()
//                            .mutate()
//                            .header("X-User-Id", userId)
//                            .header("X-User-Roles", roles)
//                            .build();
//
//            return chain.filter(exchange.mutate().request(mutatedRequest).build());
//
//        } catch (Exception ex) {
//            log.error("❌ Token parsing error → {}", ex.getMessage());
//            return unauthorized(exchange, "Invalid token");
//        }
//    }
//
//    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//
//        DataBuffer buffer = exchange.getResponse()
//                .bufferFactory()
//                .wrap(("{\"error\":\"" + message + "\"}").getBytes());
//
//        return exchange.getResponse().writeWith(Mono.just(buffer));
//    }
//
//    @Override
//    public int getOrder() {
//        return -100;   // Runs early
//    }
//}
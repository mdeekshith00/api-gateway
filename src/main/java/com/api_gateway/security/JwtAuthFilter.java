package com.api_gateway.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.common.security.JWTService;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {


    private JWTService jwtService;

	// ✅ EXPLICIT CONSTRUCTOR (THIS FIXES THE ERROR)
    public JwtAuthFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }
    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthFilter.class);


    private static final List<String> PUBLIC = List.of(
            "/user/sign-up",
            "/user/sign-in",
            "/user/refresh-token",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        log.info("🌍 [GATEWAY] Incoming path: {}", path);

        if (PUBLIC.stream().anyMatch(path::startsWith)) {
            log.info("🟢 [GATEWAY] Public endpoint → skipping JWT check");
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.info("🔍 [GATEWAY] Authorization from client: {}", auth);

        if (auth == null || !auth.startsWith("Bearer ")) {
            log.warn("⛔ [GATEWAY] No/invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = auth.substring(7);

        try {
            var claims = jwtService.extractAllClaims(token);
            log.info("✅ [GATEWAY] JWT valid. User: {}, Roles: {}",
                    claims.getSubject(), claims.get("roles"));

            ServerWebExchange updated = exchange.mutate()
                    .request(builder -> builder.headers(headers -> {
                        headers.set("Authorization", auth); // keep same
                        headers.set("X-USER-ID", claims.getSubject());
                        headers.set("X-ROLES", String.valueOf(claims.get("roles")));
                    }))
                    .build();

            return chain.filter(updated);

        } catch (Exception e) {
            log.error("❌ [GATEWAY] JWT validation failed: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;  // 🔥 run before routing
    }
}

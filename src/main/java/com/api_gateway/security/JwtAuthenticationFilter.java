package com.api_gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.AssertFalse.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	    private final JwtUtil jwtUtil;

	    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
	        super(Config.class);
	        this.jwtUtil = jwtUtil;
	    }

	    @Override
	    public GatewayFilter apply(Config config) {
	        return (exchange, chain) -> {
	            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	                return exchange.getResponse().setComplete();
	            }

	            String token = authHeader.substring(7);
	            if (!jwtUtil.validateToken(token)) {
	                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	                return exchange.getResponse().setComplete();
	            }

	            Claims claims = jwtUtil.getClaims(token);
	            ServerHttpRequest mutated = exchange.getRequest().mutate()
	                    .header("X-User-Id", claims.getSubject())
	                    .header("X-User-Roles", String.join(",", (CharSequence) claims.get("roles", List.class)))
	                    .build();

	            return chain.filter(exchange.mutate().request(mutated).build());
	        };
	    }

	    public static class Config {}
	
}

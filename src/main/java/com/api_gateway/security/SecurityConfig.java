package com.api_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)  // ✅ Disable CSRF for gateway
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/users/**").permitAll()  // allow login/signup
                        .anyExchange().permitAll()  // allow all temporarily (can secure later)
                )
                .build();
    }

}

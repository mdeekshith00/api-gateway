package com.api_gateway.security;


import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayRoutesConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // 🧍 User Service
                .route("user-service", r -> r.path("/users/**", "/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://user-service"))

                // 🏥 Hospital Service
                .route("hospital-service", r -> r.path("/hospitals/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://hospital-service"))

                // 🩸 Donor Service
                .route("donor-service", r -> r.path("/donors/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://donor-service"))

                // 🧬 Blood Stock Service
                .route("stock-service", r -> r.path("/stocks/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://stock-service"))

                // 🩺 Camp Service
                .route("camp-service", r -> r.path("/camps/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://camp-service"))

                // 🚚 Donation Service
                .route("donation-service", r -> r.path("/donations/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://donation-service"))

                .build();
    }
}

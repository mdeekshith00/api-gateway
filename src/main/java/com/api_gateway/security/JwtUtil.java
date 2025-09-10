package com.api_gateway.security;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {
    private final String secret = "5T3F45UDBDHWRGEBFVGDHEGRGDBJBD5374EBJ56EjhbfghdbwddvHERYUGFJHVEHGFGFV";  // move to config

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token); // will throw exception if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.api_gateway.security;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Value;

@Component
public class JwtUtil {
	
//	   @Value("${jwt.secret}")
//	    private String secret;
	
    private final static String SECRET_KEY = "q3F6BxZ1CZHb+y4Yw3qgRkz9C5Vql0Yq3zRZbS1yR6w=";  // move to config
    
//    public static String generateToken(String username, String role) {
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) // 2 hours
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//    
//    public Claims validateToken(String token) throws Exception {
//        try {
//            return Jwts.parser()
//                    .setSigningKey(SECRET_KEY.getBytes())
//                    .parseClaimsJws(token)
//                    .getBody();
//        } catch (SignatureException e) {
//            throw new Exception("Invalid JWT signature");
//        } catch (ExpiredJwtException e) {
//            throw new Exception("JWT token expired");
//        } catch (Exception e) {
//            throw new Exception("Invalid JWT token");
//        }
//    }
//
//    public Claims getClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET_KEY.getBytes())
//                .parseClaimsJws(token)
//                .getBody();
//    }
//    public Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                   .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
//                   .build()
//                   .parseClaimsJws(token)
//                   .getBody();
//    }
//
//    public String getUserId(Claims claims) {
//        return claims.get("userId", String.class);
//    }
//
//    public List<String> getRoles(Claims claims) {
//        return claims.get("roles", List.class);
//    }
    
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }
}

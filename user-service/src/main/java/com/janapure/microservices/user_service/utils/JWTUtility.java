package com.janapure.microservices.user_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTUtility {

    private static final String SECRET_KEY = "bG9uZ3JhbmRvbWx5Z2VuZXJhdGVkc2VjcmV0a2V5MTIzIQ";

    // Generate the signing key
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    // Generate a JWT token with username and roles
    public String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username from the token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract roles from the token
    public List<String> extractRoles(String token) {
        return (List<String>) extractClaims(token).get("roles");
    }

    // Validate the token
    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Extract claims from the token
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
package com.janapure.ecommerce_gateway.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JWTService {

    private static final String SECRETE_KEY = "bG9uZ3JhbmRvbWx5Z2VuZXJhdGVkc2VjcmV0a2V5MTIzIQ";

    private Key getSignKey() {
        // Generate a signing key using the secret key
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRETE_KEY));
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            System.out.println("................Token is valid");
        } catch (io.jsonwebtoken.SignatureException e) {
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new RuntimeException("JWT token is expired", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new RuntimeException("Malformed JWT token", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid token format", e);
        }
    }
}

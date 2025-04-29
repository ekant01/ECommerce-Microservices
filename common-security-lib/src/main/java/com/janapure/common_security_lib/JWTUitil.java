package com.janapure.common_security_lib;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.List;


public class JWTUitil {

    private static final String SECRETE_KEY = "bG9uZ3JhbmRvbWx5Z2VuZXJhdGVkc2VjcmV0a2V5MTIzIQ";

    private static Key getSignKey() {
        // Generate a signing key using the secret key
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRETE_KEY));
    }

    public static Claims extractClaims(String token) {
        // Extract claims from the JWT token
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractUsername(String token) {
        // Extract the username from the JWT token
        return extractClaims(token).getSubject();
    }

    public static boolean isTokenExpired(String token) {
        // Check if the token is expired
        return extractClaims(token).getExpiration().before(new Date());
    }

    public static boolean isTokenValid(String token) {
        // Validate the token by checking the username and expiration
        String username = extractUsername(token);
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    public static List<String> extractRoles(String token) {

        // Extract roles from the JWT token
        return (List<String>) extractClaims(token).get("roles");
    }


//    public void validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getSignKey())
//                    .build()
//                    .parseClaimsJws(token);
//            System.out.println("................Token is valid");
//        } catch (io.jsonwebtoken.SignatureException e) {
//            throw new RuntimeException("Invalid JWT signature", e);
//        } catch (io.jsonwebtoken.ExpiredJwtException e) {
//            throw new RuntimeException("JWT token is expired", e);
//        } catch (io.jsonwebtoken.MalformedJwtException e) {
//            throw new RuntimeException("Malformed JWT token", e);
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid token format", e);
//        }
//    }
}

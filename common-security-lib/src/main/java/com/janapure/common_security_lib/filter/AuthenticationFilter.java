package com.janapure.common_security_lib.filter;

import com.janapure.common_security_lib.JWTUitil;
import com.janapure.common_security_lib.config.SecurityConfig;
import com.janapure.common_security_lib.model.EUserDetails;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



public class AuthenticationFilter extends OncePerRequestFilter implements Ordered {

    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(AuthenticationFilter.class);

    private final JWTUitil jwtUtil;

    private final SecurityConfig securityConfig;

    public AuthenticationFilter(JWTUitil jwtUtil, SecurityConfig securityConfig) {
        this.jwtUtil = jwtUtil;
        this.securityConfig = securityConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("...Inside AuthenticationFilter");
        String requestURI = request.getRequestURI();
        if (securityConfig.getSkipPaths() != null && securityConfig.getSkipPaths().contains(requestURI)) {
            logger.info("Skipping authentication for path: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            handlerErrorResponse(response, "Authorization header is missing or invalid");
            return;
        }
        String token = authorization.substring(7);
        if (JWTUitil.isTokenValid(token)){
            String userName = JWTUitil.extractUsername(token);
            List<String> roles = JWTUitil.extractRoles(token);
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (roles != null && !roles.isEmpty()) {
                authorities = roles.stream()
                        .map(String::trim)
                        .filter(role -> !role.isEmpty())
                        .map(role -> new SimpleGrantedAuthority(role.trim()))
                        .collect(Collectors.toList());
            }
            EUserDetails userDetails = new EUserDetails();
            userDetails.setUserName(userName);
            userDetails.setAuthorities(authorities);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } else {
            handlerErrorResponse(response, "Invalid token");
        }

    }

    private void handlerErrorResponse(HttpServletResponse response, String s) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        try {
            response.getWriter().write("{\"error\": unAuthorized invalid Token \"" + s + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

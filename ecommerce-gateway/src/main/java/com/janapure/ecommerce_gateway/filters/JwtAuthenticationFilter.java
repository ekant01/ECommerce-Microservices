package com.janapure.ecommerce_gateway.filters;

import com.janapure.ecommerce_gateway.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JWTService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (path.contains("/user/login") || path.contains("/user/register")) {
            System.out.println("...............................skipping"+path);
            return chain.filter(exchange);
        }
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            System.out.println("...............................missing"+path);
            return this.onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
        }
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("...............................invalid"+path);
            return this.onError(exchange, "Invalid authorization header", HttpStatus.UNAUTHORIZED);
        }
        String token = authorizationHeader.substring(7);
        try {

            jwtService.validateToken(token);
            System.out.println("...............................valid"+token);
        } catch (Exception e) {
            return this.onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
        }
        return chain.filter(exchange);
    }

//    private Mono<Void> onError(ServerWebExchange exchange, String missingAuthorizationHeader, HttpStatus httpStatus) {
//        exchange.getResponse().setStatusCode(httpStatus);
//
//        return exchange.getResponse().setComplete();
//    }
    private Mono<Void> onError(ServerWebExchange exchange, String msg, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String jsonResponse = String.format("{\"error\": \"%s\"}", msg);
        byte[] bytes = jsonResponse.getBytes();
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}


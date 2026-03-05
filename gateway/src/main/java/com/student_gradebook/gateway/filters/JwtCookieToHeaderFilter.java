package com.student_gradebook.gateway.filters;

import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtCookieToHeaderFilter implements WebFilter {

    private static final String JWT_COOKIE_NAME = "AUTH_TOKEN";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/gradebook/auth/login/usos/callback",
            "/gradebook/auth/login/usos",
            "/actuator/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        // Check if Authorization header already exists
        String existingAuthHeader = request.getHeaders().getFirst(AUTH_HEADER);

        if (existingAuthHeader != null && existingAuthHeader.startsWith(BEARER_PREFIX)) {
            System.out.println("📋 Using existing Authorization header for: " + path);
            return chain.filter(exchange);
        }

        // Try to extract JWT from cookie
        HttpCookie cookie = request.getCookies() != null
                ? request.getCookies().getFirst(JWT_COOKIE_NAME)
                : null;

        if (cookie != null && cookie.getValue() != null) {
            String jwt = cookie.getValue();

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(AUTH_HEADER, BEARER_PREFIX + jwt)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

        return chain.filter(exchange);
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
package com.student_gradebook.gateway.config;

import com.student_gradebook.gateway.filters.JwtCookieToHeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/gradebook/auth/login/usos/callback",
            "/gradebook/auth/login/usos",
            "/actuator/**"
    );

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtCookieToHeaderFilter jwtCookieToHeaderFilter) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(EXCLUDED_PATHS.toArray(new String[0])).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtCookieToHeaderFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}
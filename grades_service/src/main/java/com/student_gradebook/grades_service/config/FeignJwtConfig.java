package com.student_gradebook.grades_service.config;

import com.student_gradebook.grades_service.controller.exceptions.UnAuthorizedActionException;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignJwtConfig {

    @Bean
    public RequestInterceptor authInterceptor() {
        return template -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs == null) {
                return;
            }

            String authHeader = attrs.getRequest().getHeader("Authorization");
            if (authHeader != null) {
                template.header("Authorization", authHeader);
            }
        };
    }
}
package com.student_gradebook.auth_server.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.student_gradebook.auth_server.security.JwksProvider;
import com.student_gradebook.auth_server.security.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {
    private final JwksProvider jwksProvider;
    private final JwtUtil jwtUtil;

    @GetMapping("/jwks.json")
    public Map<String, Object> jwks() {
        return new JWKSet(jwksProvider.getJwk()).toJSONObject();
    }

    @GetMapping("/me")
    public ResponseEntity<Claims> hi(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(jwtUtil.extractAllClaims(jwt.getTokenValue()));
    }
}

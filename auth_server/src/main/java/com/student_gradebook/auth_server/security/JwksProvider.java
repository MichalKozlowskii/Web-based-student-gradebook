package com.student_gradebook.auth_server.security;

import lombok.Getter;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.RSAKey;

@Component
@Getter
public class JwksProvider {

    private final RSAKey jwk;

    public JwksProvider(KeyManager keys) {
        this.jwk = new RSAKey.Builder(keys.getPublicKey())
                .keyID("auth-server-key-1")
                .build();
    }
}

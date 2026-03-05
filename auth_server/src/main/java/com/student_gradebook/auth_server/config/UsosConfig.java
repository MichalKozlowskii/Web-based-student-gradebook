package com.student_gradebook.auth_server.config;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class UsosConfig {
    @Value("${usos.consumerKey}")
    private String consumerKey;
    @Value("${usos.consumerSecret}")
    private String consumerSecret;
    @Value("${usos.requestTokenUrl}")
    private String requestTokenUrl;
    @Value("${usos.accessTokenUrl}")
    private String accessTokenUrl;
    @Value("${usos.authorizeUrl}")
    private String authorizeUrl;
    @Value("${usos.callback}")
    private String callback;

    @Bean
    public OAuth10aService usosService() {
        var api = new DefaultApi10a() {
            @Override public String getRequestTokenEndpoint() {
                String scopes = URLEncoder.encode("staff_perspective|studies", StandardCharsets.UTF_8);
                return requestTokenUrl + "?scopes=" + scopes;
            }
            @Override public String getAccessTokenEndpoint() { return accessTokenUrl; }
            @Override public String getAuthorizationBaseUrl() { return authorizeUrl; }
        };
        return new ServiceBuilder(consumerKey)
                .apiSecret(consumerSecret)
                .callback(callback)
                .build(api);
    }
}

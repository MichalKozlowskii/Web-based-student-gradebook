package com.student_gradebook.auth_server.config;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.github.scribejava.core.model.Verb;


@Configuration
public class WebClientConfig {

    public static Verb convert(HttpMethod method) {
        if (method == HttpMethod.GET) return Verb.GET;
        if (method == HttpMethod.POST) return Verb.POST;
        if (method == HttpMethod.PUT) return Verb.PUT;
        if (method == HttpMethod.DELETE) return Verb.DELETE;
        if (method == HttpMethod.PATCH) return Verb.PATCH;
        throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }

    @Bean
    public WebClient webClient(OAuth10aService oAuthService) {
        return WebClient.builder()
                .baseUrl("https://usosapps.uwr.edu.pl")
                .filter((request, next) -> {

                    String userToken = request.headers().getFirst("X-User-Token");
                    String userSecret = request.headers().getFirst("X-User-Secret");

                    OAuth1AccessToken accessToken = new OAuth1AccessToken(userToken, userSecret);

                    // Build ScribeJava request
                    OAuthRequest oauthRequest = new OAuthRequest(
                            convert(request.method()),
                            request.url().toString()
                    );

                    try {
                        // Sign the request (adds all oauth_* params)
                        oAuthService.signRequest(accessToken, oauthRequest);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }

                    // Apply the Authorization header to WebClient request
                    String signedHeaders = oauthRequest.getHeaders().get("Authorization");

                    ClientRequest newRequest = ClientRequest.from(request)
                            .headers(h -> h.set("Authorization", signedHeaders))
                            .build();

                    return next.exchange(newRequest);
                })
                .codecs(config ->
                        config.defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024) // 10MB
                )
                .build();
    }
}

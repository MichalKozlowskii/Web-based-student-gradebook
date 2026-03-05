package com.student_gradebook.auth_server.controller;

import com.student_gradebook.auth_server.dto.OAuthCallbackResult;
import com.student_gradebook.auth_server.controller.exceptions.OAuthException;
import com.student_gradebook.auth_server.controller.exceptions.OAuthRateLimitException;
import com.student_gradebook.auth_server.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UsosAuthController {
    private final OAuthService oAuthService;

    @Value("${token.expiration}")
    private int tokenExpirationTime;
    @Value("${frontend.success-url}")
    private String frontendSuccessUrl;
    @Value("${frontend.error-url}")
    private String frontendErrorUrl;

    @GetMapping("/login/usos")
    public RedirectView loginViaUsos() {
        try {
            String authUrl = oAuthService.initiateLogin();
            return new RedirectView(authUrl);
        } catch (OAuthRateLimitException e) {
            return new RedirectView(frontendErrorUrl + "?error=rate_limited");
        } catch (OAuthException e) {
            return new RedirectView(frontendErrorUrl + "?error=usos_unavailable");
        }
    }

    @GetMapping("/login/usos/callback")
    public void usosCallback(
            @RequestParam("oauth_token") @Valid @NotBlank @Size(max = 500) String oauthToken,
            @RequestParam("oauth_verifier") @Valid @NotBlank @Size(max = 500) String oauthVerifier,
            HttpServletResponse response) throws IOException {

        OAuthCallbackResult result = oAuthService.handleCallback(oauthToken, oauthVerifier);

        if (result.isSuccess()) {
            setAuthCookie(response, result.getJwt());
            response.sendRedirect(frontendSuccessUrl);
        } else {
            response.sendRedirect(frontendErrorUrl + "?error=" + result.getErrorCode());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Jwt jwt, HttpServletResponse response) {
        clearAuthCookie(response);
        oAuthService.logOut(jwt);

        return ResponseEntity.ok().body(Map.of("message", "Logged out"));
    }

    private void setAuthCookie(HttpServletResponse response, String jwt) {
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", jwt)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(tokenExpirationTime)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearAuthCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
package com.student_gradebook.auth_server.service;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.student_gradebook.auth_server.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.view.RedirectView;

public interface UserService {
    User findOrCreateUser(OAuth1AccessToken accessToken);
    void removeAccessToken(String userId);
    OAuth1AccessToken fetchAccessToken(Jwt jwt);
    String generateJwt(User user);
}

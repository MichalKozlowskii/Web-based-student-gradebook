package com.student_gradebook.auth_server.service;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.student_gradebook.auth_server.controller.exceptions.UnsupportedUsosUserException;
import com.student_gradebook.auth_server.entity.User;
import com.student_gradebook.auth_server.enums.Role;
import com.student_gradebook.auth_server.records.UserDetailsResponse;
import com.student_gradebook.auth_server.repository.UserRepository;
import com.student_gradebook.auth_server.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UsosClient usosClient;
    private final JwtUtil jwtUtil;

    @Override
    public User findOrCreateUser(OAuth1AccessToken accessToken) {
        UserDetailsResponse userDetails = usosClient.getUserDetails(accessToken);

        Role role;
        if (userDetails.studentStatus() == 2) {
            role = Role.STUDENT;
        } else if (userDetails.staffStatus() == 2) {
            role = Role.LECTURER;
        } else {
            throw new UnsupportedUsosUserException("USOS account is neither student nor a lecturer");
        }

        Optional<User> repoUser = userRepository.findById(userDetails.id());

        if (repoUser.isPresent()) {
            User user = repoUser.get();

            if (role != user.getRole()) {
                user.setRole(role);
            }

            if (user.getRole() == Role.LECTURER) {
                user.setAccessToken(accessToken);
                return userRepository.save(repoUser.get());
            }

            return user;
        }

        return userRepository.save(
                User.builder()
                        .id(userDetails.id())
                        .firstName(userDetails.firstName())
                        .lastName(userDetails.lastName().substring(0, 3))
                        .role(role)
                        .accessToken(role == Role.LECTURER ? accessToken : null)
                        .build()
        );
    }

    @Override
    public void removeAccessToken(String userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent() && user.get().getRole() == Role.LECTURER) {
            user.get().setAccessToken(null);
            userRepository.save(user.get());
        }
    }

    @Override
    public OAuth1AccessToken fetchAccessToken(Jwt jwt) {
        String id = jwt.getClaimAsString("id");
        User user = userRepository.findById(id).orElse(null);

        return user != null ? user.getAccessToken() : null;
    }

    @Override
    public String generateJwt(User user) {
        return jwtUtil.generateToken(user, TimeUnit.HOURS.toMillis(2));
    }
}

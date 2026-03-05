package com.student_gradebook.auth_server.service;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.student_gradebook.auth_server.controller.exceptions.UnsupportedUsosUserException;
import com.student_gradebook.auth_server.entity.User;
import com.student_gradebook.auth_server.enums.Role;
import com.student_gradebook.auth_server.records.UserDetailsResponse;
import com.student_gradebook.auth_server.repository.UserRepository;
import com.student_gradebook.auth_server.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsosClient usosClient;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private OAuth1AccessToken accessToken;

    @Test
    void shouldUpdateExistingUserAccessToken() {
        UserDetailsResponse response = new UserDetailsResponse(
                "1",
                "John",
                "Doe",
                0,
                2
        );

        User existingUser = User.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .role(Role.LECTURER)
                .build();

        when(usosClient.getUserDetails(accessToken)).thenReturn(response);
        when(userRepository.findById("1")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.findOrCreateUser(accessToken);

        assertEquals("1", result.getId());
        assertEquals(accessToken, result.getAccessToken());
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldCreateNewStudentUser() {
        UserDetailsResponse response = new UserDetailsResponse(
                "2",
                "Anna",
                "Nowak",
                2,
                0
        );

        when(usosClient.getUserDetails(accessToken)).thenReturn(response);
        when(userRepository.findById("2")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.findOrCreateUser(accessToken);

        assertEquals("Now", result.getLastName());
    }

    @Test
    void shouldCreateNewLecturerUser() {
        UserDetailsResponse response = new UserDetailsResponse(
                "3",
                "Mark",
                "Smith",
                0,
                2
        );

        when(usosClient.getUserDetails(accessToken)).thenReturn(response);
        when(userRepository.findById("3")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.findOrCreateUser(accessToken);

        assertEquals(Role.LECTURER, result.getRole());
    }

    @Test
    void shouldThrowExceptionForUnsupportedUser() {
        UserDetailsResponse response = new UserDetailsResponse(
                "4",
                "Tom",
                "Lee",
                0,
                0
        );

        when(usosClient.getUserDetails(accessToken)).thenReturn(response);

        assertThrows(
                UnsupportedUsosUserException.class,
                () -> userService.findOrCreateUser(accessToken)
        );
    }

    @Test
    void shouldGenerateJwt() {
        User user = User.builder().id("5").build();
        when(jwtUtil.generateToken(eq(user), anyLong())).thenReturn("jwt");

        String token = userService.generateJwt(user);

        assertEquals("jwt", token);
        verify(jwtUtil).generateToken(eq(user), eq(TimeUnit.HOURS.toMillis(2)));
    }
}

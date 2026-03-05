package com.student_gradebook.auth_server.entity;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.student_gradebook.auth_server.encryption.OAuth1AccessTokenConverter;
import com.student_gradebook.auth_server.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(length = 10)
    private String id;

    @Column(name = "first_name", length = 16)
    private String firstName;

    @Column(name = "last_name", length = 3)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10)
    private Role role;

    @Convert(converter = OAuth1AccessTokenConverter.class)
    @Column(name = "access_token", length = 1024)
    private OAuth1AccessToken accessToken;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}

package com.student_gradebook.auth_server.repository;

import com.student_gradebook.auth_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}

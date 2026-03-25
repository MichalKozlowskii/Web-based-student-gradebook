package com.student_gradebook.grades_service.repository;

import com.student_gradebook.grades_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByLecturerId(String lecturerId);
}

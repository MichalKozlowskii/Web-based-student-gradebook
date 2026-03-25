package com.student_gradebook.grades_service.service;

import com.student_gradebook.grades_service.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    Long createCategory(CategoryDto categoryDto, String lecturerId);
    CategoryDto fetchCategory(Long categoryId, String lecturerId);
    List<CategoryDto> fetchCategories(String lecturerId);
    void updateCategory(Long categoryId, CategoryDto categoryDto, String lecturerId);
    void deleteCategory(Long categoryId, String lecturerId);
}

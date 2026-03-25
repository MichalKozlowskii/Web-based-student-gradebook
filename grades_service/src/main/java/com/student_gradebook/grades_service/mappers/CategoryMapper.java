package com.student_gradebook.grades_service.mappers;

import com.student_gradebook.grades_service.dto.CategoryDto;
import com.student_gradebook.grades_service.entity.Category;

public class CategoryMapper {
    public static Category CategoryDtoToCategory(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .weight(categoryDto.getWeight())
                .build();
    }

    public static CategoryDto CategoryToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .weight(category.getWeight())
                .createdAt(category.getCreatedAt())
                .lastUpdated(category.getLastUpdated())
                .build();
    }
}

package com.student_gradebook.grades_service.service;

import com.student_gradebook.grades_service.controller.exceptions.ResourceNotFoundException;
import com.student_gradebook.grades_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.grades_service.dto.CategoryDto;
import com.student_gradebook.grades_service.entity.Category;
import com.student_gradebook.grades_service.mappers.CategoryMapper;
import com.student_gradebook.grades_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public Long createCategory(CategoryDto categoryDto, String lecturerId) {
        Category category = CategoryMapper.CategoryDtoToCategory(categoryDto);
        category.setLecturerId(lecturerId);

        return categoryRepository.save(category).getId();
    }

    @Override
    public CategoryDto fetchCategory(Long categoryId, String lecturerId) {
        Category fetched = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category not found"));

        if (!Objects.equals(fetched.getLecturerId(), lecturerId))
            throw new UnAuthorizedActionException("You don't have permissions.");

        return CategoryMapper.CategoryToCategoryDto(fetched);
    }

    @Override
    public List<CategoryDto> fetchCategories(String lecturerId) {
        return categoryRepository.findAllByLecturerId(lecturerId).stream()
                .map(CategoryMapper::CategoryToCategoryDto)
                .toList();
    }

    @Override
    public void updateCategory(Long categoryId, CategoryDto categoryDto, String lecturerId) {
        Category fetched = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category not found"));

        if (!Objects.equals(fetched.getLecturerId(), lecturerId))
            throw new UnAuthorizedActionException("You don't have permissions.");

        fetched.setName(categoryDto.getName());
        fetched.setWeight(categoryDto.getWeight());

        categoryRepository.save(fetched);
    }

    @Override
    public void deleteCategory(Long categoryId, String lecturerId) {
        Category fetched = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category not found"));

        if (!Objects.equals(fetched.getLecturerId(), lecturerId))
            throw new UnAuthorizedActionException("You don't have permissions.");

        categoryRepository.delete(fetched);
    }
}

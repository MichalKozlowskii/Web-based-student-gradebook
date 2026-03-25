package com.student_gradebook.grades_service.service;

import com.student_gradebook.grades_service.controller.exceptions.ResourceNotFoundException;
import com.student_gradebook.grades_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.grades_service.dto.CategoryDto;
import com.student_gradebook.grades_service.entity.Category;
import com.student_gradebook.grades_service.mappers.CategoryMapper;
import com.student_gradebook.grades_service.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --------- createCategory ---------

    @Test
    void createCategory_shouldSaveAndReturnId() {
        CategoryDto dto = CategoryDto.builder()
                .name("Homework")
                .weight(5)
                .build();

        Category mapped = Category.builder()
                .name("Homework")
                .weight(5)
                .build();

        Category saved = Category.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .lecturerId("LEC001")
                .build();

        try (MockedStatic<CategoryMapper> mapper = mockStatic(CategoryMapper.class)) {
            mapper.when(() -> CategoryMapper.CategoryDtoToCategory(dto)).thenReturn(mapped);
            when(categoryRepository.save(mapped)).thenReturn(saved);

            Long result = categoryService.createCategory(dto, "LEC001");

            assertEquals(1L, result);
            assertEquals("LEC001", mapped.getLecturerId());
            verify(categoryRepository).save(mapped);
        }
    }

    // --------- fetchCategory ---------

    @Test
    void fetchCategory_shouldReturnDto_whenExistsAndOwned() {
        Category category = Category.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .lecturerId("LEC001")
                .build();

        CategoryDto dto = CategoryDto.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        try (MockedStatic<CategoryMapper> mapper = mockStatic(CategoryMapper.class)) {
            mapper.when(() -> CategoryMapper.CategoryToCategoryDto(category)).thenReturn(dto);

            CategoryDto result = categoryService.fetchCategory(1L, "LEC001");

            assertEquals(dto, result);
        }
    }

    @Test
    void fetchCategory_shouldThrow_whenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.fetchCategory(1L, "LEC001"));
    }

    @Test
    void fetchCategory_shouldThrow_whenNotOwned() {
        Category category = Category.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .lecturerId("LEC001")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(UnAuthorizedActionException.class, () -> categoryService.fetchCategory(1L, "LEC999"));
    }

    // --------- fetchCategories ---------

    @Test
    void fetchCategories_shouldReturnMappedList() {
        Category category = Category.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .lecturerId("LEC001")
                .build();

        CategoryDto dto = CategoryDto.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .build();

        when(categoryRepository.findAllByLecturerId("LEC001")).thenReturn(List.of(category));

        try (MockedStatic<CategoryMapper> mapper = mockStatic(CategoryMapper.class)) {
            mapper.when(() -> CategoryMapper.CategoryToCategoryDto(category)).thenReturn(dto);

            List<CategoryDto> result = categoryService.fetchCategories("LEC001");

            assertEquals(1, result.size());
            assertEquals("Homework", result.get(0).getName());
        }
    }

    @Test
    void fetchCategories_shouldReturnEmptyList_whenNoCategories() {
        when(categoryRepository.findAllByLecturerId("LEC001")).thenReturn(List.of());

        List<CategoryDto> result = categoryService.fetchCategories("LEC001");

        assertTrue(result.isEmpty());
    }

    // --------- updateCategory ---------

    @Test
    void updateCategory_shouldUpdateFields_whenExistsAndOwned() {
        Category category = Category.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .lecturerId("LEC001")
                .build();

        CategoryDto dto = CategoryDto.builder()
                .name("Labs")
                .weight(8)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.updateCategory(1L, dto, "LEC001");

        assertEquals("Labs", category.getName());
        assertEquals(8, category.getWeight());
        verify(categoryRepository).save(category);
    }

    @Test
    void updateCategory_shouldThrow_whenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        CategoryDto dto = CategoryDto.builder().name("Labs").weight(8).build();

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(1L, dto, "LEC001"));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_shouldThrow_whenNotOwned() {
        Category category = Category.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .lecturerId("LEC001")
                .build();

        CategoryDto dto = CategoryDto.builder().name("Labs").weight(8).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(UnAuthorizedActionException.class, () -> categoryService.updateCategory(1L, dto, "LEC999"));
        verify(categoryRepository, never()).save(any());
    }

    // --------- deleteCategory ---------

    @Test
    void deleteCategory_shouldDelete_whenExistsAndOwned() {
        Category category = Category.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .lecturerId("LEC001")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L, "LEC001");

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_shouldThrow_whenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L, "LEC001"));
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_shouldThrow_whenNotOwned() {
        Category category = Category.builder()
                .id(1L)
                .name("Homework")
                .weight(5)
                .lecturerId("LEC001")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(UnAuthorizedActionException.class, () -> categoryService.deleteCategory(1L, "LEC999"));
        verify(categoryRepository, never()).delete(any());
    }
}
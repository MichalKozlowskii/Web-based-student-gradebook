package com.student_gradebook.grades_service.controller;

import com.student_gradebook.grades_service.dto.CategoryDto;
import com.student_gradebook.grades_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryDto categoryDto,
                                                 @AuthenticationPrincipal Jwt jwt) {
        String lecturerId = jwt.getClaimAsString("id");

        Long newCategoryId = categoryService.createCategory(categoryDto, lecturerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("location", "/api/category/fetch/" + newCategoryId)
                .body("Category created successfully.");
    }

    @GetMapping("/fetch/{categoryId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<CategoryDto> fetchCategory(@PathVariable("categoryId") Long categoryId,
                                                @AuthenticationPrincipal Jwt jwt) {
        String lecturerId = jwt.getClaimAsString("id");

        CategoryDto categoryDto = categoryService.fetchCategory(categoryId, lecturerId);

        return ResponseEntity
                .ok()
                .body(categoryDto);
    }

    @GetMapping("/fetch")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<CategoryDto>> fetchCategory(@AuthenticationPrincipal Jwt jwt) {
        String lecturerId = jwt.getClaimAsString("id");

        List<CategoryDto> categoryDto = categoryService.fetchCategories(lecturerId);

        return ResponseEntity
                .ok()
                .body(categoryDto);
    }

    @PutMapping("/update/{categoryId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> updateCategory(@PathVariable("categoryId") Long categoryId,
                                                 @Valid @RequestBody CategoryDto categoryDto,
                                                 @AuthenticationPrincipal Jwt jwt) {
        String lecturerId = jwt.getClaimAsString("id");

        categoryService.updateCategory(categoryId, categoryDto, lecturerId);

        return ResponseEntity
                .ok()
                .body("Category updated successfully");
    }

    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> updateCategory(@PathVariable("categoryId") Long categoryId,
                                                 @AuthenticationPrincipal Jwt jwt) {
        String lecturerId = jwt.getClaimAsString("id");

        categoryService.deleteCategory(categoryId, lecturerId);

        return ResponseEntity
                .ok()
                .body("Category deleted successfully.");
    }
}

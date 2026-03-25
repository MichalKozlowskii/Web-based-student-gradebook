package com.student_gradebook.grades_service.controller;

import com.student_gradebook.grades_service.dto.ExamGradeCreationDto;
import com.student_gradebook.grades_service.dto.GradeCreationDto;
import com.student_gradebook.grades_service.dto.GradeEditionDto;
import com.student_gradebook.grades_service.dto.GradeViewDto;
import com.student_gradebook.grades_service.dto.gradesList.GradeListDto;
import com.student_gradebook.grades_service.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GradesController {
    private final GradeService gradeService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> addGrade(@Valid @RequestBody GradeCreationDto gradeCreationDto) {
        gradeService.addGrade(gradeCreationDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Grade added!");
    }

    @PostMapping("/add/exam")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> addGradeFromExam(@Valid @RequestBody ExamGradeCreationDto examGradeCreationDto) {
        gradeService.addGradeFromExam(examGradeCreationDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Grade added!");
    }

    @PostMapping("/add/list")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> addGradesFromList(@Valid @RequestBody GradeListDto gradeListDto) {
        if (!gradeService.addGradesFromList(gradeListDto)) {
            return ResponseEntity
                    .badRequest()
                    .body("No valid grades to save.");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Grades added!");
    }

    @PatchMapping("/update/{gradeId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> updateGrade(@PathVariable("gradeId") UUID gradeId,
                                            @Valid @RequestBody GradeEditionDto gradeEditionDto) {
        gradeService.updateGrade(gradeId, gradeEditionDto);
        return ResponseEntity.ok()
                .body("Grade updated!");
    }

    @DeleteMapping("/delete/{gradeId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> deleteGrade(@PathVariable("gradeId") UUID gradeId) {

        gradeService.deleteGrade(gradeId);
        return ResponseEntity.ok()
                .body("Grade deleted!");
    }

    @GetMapping("/fetch/lastActivity")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<GradeViewDto>> fetchLastStudentGrades(@AuthenticationPrincipal Jwt jwt) {
        String studentId = jwt.getClaimAsString("id");
        List<GradeViewDto> grades = gradeService.fetchLastStudentGrades(studentId);

        return ResponseEntity.ok()
                .body(grades);
    }

    @GetMapping("/fetch/course/{courseUnitId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<GradeViewDto>> fetchStudentGrades(@AuthenticationPrincipal Jwt jwt,
                                                                 @PathVariable("courseUnitId") String courseUnitId) {
        String studentId = jwt.getClaimAsString("id");
        List<GradeViewDto> grades = gradeService.fetchStudentGrades(studentId, courseUnitId);

        return ResponseEntity.ok()
                .body(grades);
    }

    @GetMapping("/fetch/course/{courseUnitId}/{groupNumber}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Map<String, List<GradeViewDto>>> fetchGradesInGroup(@PathVariable("courseUnitId") String courseUnitId,
                                                                              @PathVariable("groupNumber") Integer groupNumber) {
        Map<String, List<GradeViewDto>> gradesMap = gradeService.fetchGradesInGroup(courseUnitId, groupNumber);

        return ResponseEntity.ok()
                .body(gradesMap);
    }
}

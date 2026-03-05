package com.student_gradebook.exams_service.controller;

import com.student_gradebook.exams_service.dto.ExamDto;
import com.student_gradebook.exams_service.dto.ScanResultDto;
import com.student_gradebook.exams_service.service.ExamAnalysisService;
import com.student_gradebook.exams_service.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;
    private final ExamAnalysisService examAnalysisService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> createExam(@AuthenticationPrincipal Jwt jwt,
                                             @Valid @RequestBody ExamDto examDto) {
        String lecturerId = jwt.getClaimAsString("id");

        UUID newExamId = examService.createExam(examDto, lecturerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("location", "/api/fetch/" + newExamId)
                .body("Exam created!");
    }

    @GetMapping("/fetch/{examId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<ExamDto> fetchExam(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable("examId") UUID examId) {
        String lecturerId = jwt.getClaimAsString("id");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(examService.fetchExam(examId, lecturerId));
    }

    @GetMapping("/fetch")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<ExamDto>> fetchExams(@AuthenticationPrincipal Jwt jwt) {
        String lecturerId = jwt.getClaimAsString("id");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(examService.fetchExams(lecturerId));
    }

    @PutMapping("/update/{examId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> updateExam(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable("examId") UUID examId,
                                             @Valid @RequestBody ExamDto examDto) {
        String lecturerId = jwt.getClaimAsString("id");

        examService.updateExam(examId, examDto, lecturerId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Exam updated successfully!");
    }

    @DeleteMapping("/delete/{examId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> deleteExam(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable("examId") UUID examId) {
        String lecturerId = jwt.getClaimAsString("id");

        examService.deleteExam(examId, lecturerId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Exam deleted successfully!");
    }

    @GetMapping(value = "/generateTable/{examId}", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<byte[]> generateTable(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("examId") UUID examId) {

        String lecturerId = jwt.getClaimAsString("id");

        byte[] tableImage = examAnalysisService.addTable(examId, lecturerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDisposition(
                ContentDisposition.inline().filename("exam-table.png").build()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(tableImage);
    }

    @PostMapping("/analyze/{examId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<ScanResultDto> scanExam(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestParam("file") MultipartFile image,
                                                  @PathVariable("examId") UUID examId) {
        String lecturerId = jwt.getClaimAsString("id");

        ScanResultDto scanResultDto = examAnalysisService.analyzeExam(image, examId, lecturerId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(scanResultDto);
    }

    @PostMapping("/grade/{examId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> gradeExam(@AuthenticationPrincipal Jwt jwt,
                                            @Valid @RequestBody ScanResultDto scanResultDto,
                                            @PathVariable("examId") UUID examId) {
        String lecturerId = jwt.getClaimAsString("id");

        examService.gradeExam(examId, lecturerId, scanResultDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Grade added successfully!");
    }
}

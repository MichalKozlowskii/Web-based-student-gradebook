package com.student_gradebook.summary_service.controller;

import com.student_gradebook.summary_service.dto.StudentSummaryDto;
import com.student_gradebook.summary_service.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SummaryController {
    private final SummaryService summaryService;

    @GetMapping("/get/{courseUnitId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentSummaryDto> studentSummary(@PathVariable("courseUnitId") String courseUnitId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(summaryService.getStudentSummary(courseUnitId));
    }

    @GetMapping("/get/{courseUnitId}/{groupNumber}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Map<String, StudentSummaryDto>> lecturerSummary(@PathVariable("courseUnitId") String courseUnitId,
                                                                          @PathVariable("groupNumber") Integer groupNumber) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(summaryService.getLecturerSummary(courseUnitId, groupNumber));
    }
}

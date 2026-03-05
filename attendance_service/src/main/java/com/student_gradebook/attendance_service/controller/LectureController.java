package com.student_gradebook.attendance_service.controller;

import com.student_gradebook.attendance_service.dto.LectureDto;
import com.student_gradebook.attendance_service.dto.LecturePreviewDto;
import com.student_gradebook.attendance_service.dto.LectureUpdateDto;
import com.student_gradebook.attendance_service.service.LectureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> createNewLecture(@Valid @RequestBody LectureDto lectureDto) {
        lectureService.createNewLecture(lectureDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Lecture created successfully!");
    }

    @GetMapping("/fetch/{courseUnitId}/{groupNumber}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<LecturePreviewDto>> fetchLectures(@PathVariable("courseUnitId") String courseUnitId,
                                                                 @PathVariable("groupNumber") Integer groupNumber) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lectureService.fetchLectures(courseUnitId, groupNumber));
    }

    @GetMapping("/fetch/{lectureId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity fetchLecture(@PathVariable("lectureId") UUID lectureId) {
        LectureDto lectureDto = lectureService.fetchLecture(lectureId);

        if (lectureDto == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lectureDto);
    }

    @DeleteMapping("/delete/{lectureId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> deleteLecture(@PathVariable("lectureId") UUID lectureId) {
        if (!lectureService.deleteLecture(lectureId)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Lecture not found.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Lecture deleted!");
    }

    @PatchMapping("/update/{lectureId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> updateLecture(@PathVariable("lectureId") UUID lectureId,
                                                @Valid @RequestBody LectureUpdateDto lectureUpdateDto) {
        if (!lectureService.updateLecture(lectureId, lectureUpdateDto)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Lecture not found.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Lecture updated!");
    }
}

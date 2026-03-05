package com.student_gradebook.attendance_service.mappers;

import com.student_gradebook.attendance_service.dto.LectureDto;
import com.student_gradebook.attendance_service.dto.LecturePreviewDto;
import com.student_gradebook.attendance_service.entity.Lecture;

import java.util.stream.Collectors;

public class LectureMapper {
    public static LecturePreviewDto lectureToLecturePreviewDto(Lecture lecture) {
        return LecturePreviewDto.builder()
                .id(lecture.getId())
                .courseUnitId(lecture.getCourseUnitId())
                .groupNumber(lecture.getGroupNumber())
                .createdAt(lecture.getCreatedAt())
                .lastUpdated(lecture.getLastUpdated())
                .build();
    }

    public static LectureDto lectureToLectureDto(Lecture lecture) {
        return LectureDto.builder()
                .id(lecture.getId())
                .courseUnitId(lecture.getCourseUnitId())
                .groupNumber(lecture.getGroupNumber())
                .termId(lecture.getTermId())
                .attendanceList(lecture.getAttendanceList().stream()
                        .map(AttendanceMapper::attendanceToAttendanceDto)
                        .collect(Collectors.toSet()))
                .createdAt(lecture.getCreatedAt())
                .lastUpdated(lecture.getLastUpdated())
                .build();
    }
}

package com.student_gradebook.attendance_service.service;

import com.student_gradebook.attendance_service.dto.LectureDto;
import com.student_gradebook.attendance_service.dto.LecturePreviewDto;
import com.student_gradebook.attendance_service.dto.LectureUpdateDto;

import java.util.List;
import java.util.UUID;

public interface LectureService {
    void createNewLecture(LectureDto lectureDto);

    List<LecturePreviewDto> fetchLectures(String courseUnitId, Integer groupNumber);

    LectureDto fetchLecture(UUID lectureId);

    boolean deleteLecture(UUID lectureId);
    boolean updateLecture(UUID lectureId, LectureUpdateDto lectureUpdateDto);
}

package com.student_gradebook.summary_service.service;

import com.student_gradebook.summary_service.dto.StudentSummaryDto;

import java.util.Map;

public interface SummaryService {

    StudentSummaryDto getStudentSummary(String courseUnitId);

    Map<String, StudentSummaryDto> getLecturerSummary(String courseUnitId, Integer groupNumber);
}

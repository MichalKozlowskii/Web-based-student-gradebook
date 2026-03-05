package com.student_gradebook.summary_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentSummaryDto {
    private double gradesMean;
    private int plusCount;
    private int minusCount;
    private int present;
    private int absent;
    private int excused;
}

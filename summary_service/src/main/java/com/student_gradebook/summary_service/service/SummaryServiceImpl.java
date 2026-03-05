    package com.student_gradebook.summary_service.service;

    import com.student_gradebook.summary_service.controller.exceptions.NoResponseFromApiException;
    import com.student_gradebook.summary_service.dto.StudentSummaryDto;
    import com.student_gradebook.summary_service.enums.Status;
    import com.student_gradebook.summary_service.records.AttendanceRecord;
    import com.student_gradebook.summary_service.records.GradeRecord;
    import com.student_gradebook.summary_service.service.client.AttendanceFeignClient;
    import com.student_gradebook.summary_service.service.client.GradesFeignClient;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.math.BigDecimal;
    import java.math.RoundingMode;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @Service
    @RequiredArgsConstructor
    public class SummaryServiceImpl implements SummaryService {
        private final GradesFeignClient gradesFeignClient;
        private final AttendanceFeignClient attendanceFeignClient;

        @Override
        public StudentSummaryDto getStudentSummary(String courseUnitId) {
            List<GradeRecord> grades = gradesFeignClient.fetchStudentsGrades(courseUnitId);
            if (grades == null) {
                throw new NoResponseFromApiException("Couldn't get response from grades microservice.");
            }

            List<AttendanceRecord> attendance = attendanceFeignClient.fetchStudentsAttendance(courseUnitId);
            if (attendance == null) {
                throw new NoResponseFromApiException("Couldn't get response from attendance microservice.");
            }

            return calculateStudentSummary(grades, attendance);
        }

        @Override
        public Map<String, StudentSummaryDto> getLecturerSummary(String courseUnitId, Integer groupNumber) {
            Map<String, List<GradeRecord>> gradesMap = gradesFeignClient.fetchGradesInGroup(courseUnitId, groupNumber);
            if (gradesMap == null) {
                throw new NoResponseFromApiException("Couldn't get response from grades microservice.");
            }

            Map<String, List<AttendanceRecord>> attendanceMap = attendanceFeignClient
                    .fetchAttendanceInGroup(courseUnitId, groupNumber);
            if (attendanceMap == null) {
                throw new NoResponseFromApiException("Couldn't get response from attendance microservice.");
            }

            Map<String, StudentSummaryDto> summaryMap = new HashMap<>();
            for (String studentId : gradesMap.keySet()) {
                summaryMap.put(studentId, calculateStudentSummary(gradesMap.get(studentId), attendanceMap.get(studentId)));
            }

            return summaryMap;
        }

        private StudentSummaryDto calculateStudentSummary(List<GradeRecord> grades, List<AttendanceRecord> attendance) {
            int pluses = 0;
            int minuses = 0;
            double mean = 0.0;
            int gradesAmount = 0;
            if (!grades.isEmpty()) {
                double sum = 0.0;
                for (GradeRecord gradeRecord : grades) {
                    if (gradeRecord.grade().equals("0.1")) {
                        pluses++;
                    }
                    else if (gradeRecord.grade().equals("0.01")) {
                        minuses++;
                    }
                    else {
                        sum += Double.parseDouble(gradeRecord.grade());
                        gradesAmount++;
                    }
                }

                if (gradesAmount > 0) {
                    mean = new BigDecimal(sum / gradesAmount)
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                }
            }

            int present = 0;
            int absent = 0;
            int excused = 0;

            if (!attendance.isEmpty()) {
                for (AttendanceRecord attendanceRecord : attendance) {
                    switch(attendanceRecord.status()) {
                        case Status.PRESENT -> present++;
                        case Status.ABSENT -> absent++;
                        case EXCUSED -> excused++;
                    }
                }
            }

            return StudentSummaryDto.builder()
                    .gradesMean(mean)
                    .present(present)
                    .absent(absent)
                    .excused(excused)
                    .plusCount(pluses)
                    .minusCount(minuses)
                    .build();
        }
    }

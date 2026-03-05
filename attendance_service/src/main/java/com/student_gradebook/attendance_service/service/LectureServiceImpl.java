package com.student_gradebook.attendance_service.service;

import com.student_gradebook.attendance_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.attendance_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.attendance_service.dto.AttendanceDto;
import com.student_gradebook.attendance_service.dto.LectureDto;
import com.student_gradebook.attendance_service.dto.LecturePreviewDto;
import com.student_gradebook.attendance_service.dto.LectureUpdateDto;
import com.student_gradebook.attendance_service.entity.Attendance;
import com.student_gradebook.attendance_service.entity.Lecture;
import com.student_gradebook.attendance_service.enums.Status;
import com.student_gradebook.attendance_service.mappers.LectureMapper;
import com.student_gradebook.attendance_service.repository.LectureRepository;
import com.student_gradebook.attendance_service.service.client.AttendanceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {
    private final TermService termService;
    private final AttendanceFeignClient attendanceFeignClient;
    private final LectureRepository lectureRepository;

    private List<String> fetchStudentIdsFromApi(String courseUnitId, Integer groupNumber) {
        List<String> studentIds = attendanceFeignClient.fetchStudentIds(courseUnitId, groupNumber);
        if (studentIds == null) {
            throw new NoResponseFromApiException("Couldn't get response from groups microservice.");
        }
        if (studentIds.isEmpty()) {
            throw new UnAuthorizedActionException("You don't teach this group or group doesn't exist.");
        }

        return studentIds;
    }

    private void checkPermission(String courseUnitId, Integer groupNumber) {
        Boolean isAuthorised = attendanceFeignClient.isAuthorisedInGroup(courseUnitId, groupNumber);
        if (isAuthorised == null) {
            throw new NoResponseFromApiException("Couldn't get response from groups microservice.");
        }

        if (!isAuthorised) {
            throw new UnAuthorizedActionException("You don't teach this group or group doesn't exist.");
        }
    }

    @Override
    public void createNewLecture(LectureDto lectureDto) {
        List<String> studentIds = fetchStudentIdsFromApi(lectureDto.getCourseUnitId(), lectureDto.getGroupNumber());

        Map<String, AttendanceDto> attendanceByStudentId =
                lectureDto.getAttendanceList()
                        .stream()
                        .collect(Collectors.toMap(
                                AttendanceDto::getStudentId,
                                Function.identity()
                        ));

        Lecture lecture = Lecture.builder()
                .courseUnitId(lectureDto.getCourseUnitId())
                .groupNumber(lectureDto.getGroupNumber())
                .termId(termService.findActiveTerm().getId())
                .build();

        Set<Attendance> attendanceEntities = new HashSet<>();

        for (String studentId : studentIds) {
            AttendanceDto dto = attendanceByStudentId.get(studentId);

            Status status = Status.ABSENT;
            if (dto != null && dto.getStatus() != null) {
                status = dto.getStatus();
            }

            Attendance attendance = Attendance.builder()
                    .studentId(studentId)
                    .status(status)
                    .lecture(lecture)
                    .build();

            attendanceEntities.add(attendance);
        }

        lecture.setAttendanceList(attendanceEntities);

        lectureRepository.save(lecture);
    }

    @Override
    public List<LecturePreviewDto> fetchLectures(String courseUnitId, Integer groupNumber) {
        checkPermission(courseUnitId, groupNumber);

        List<Lecture> lectures = lectureRepository.findAllByCourseUnitIdAndGroupNumberAndTermId(courseUnitId,
                groupNumber, termService.findActiveTerm().getId());

        return lectures.stream()
                .map(LectureMapper::lectureToLecturePreviewDto)
                .toList();
    }

    @Override
    public LectureDto fetchLecture(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElse(null);
        if (lecture == null) return null;

        checkPermission(lecture.getCourseUnitId(), lecture.getGroupNumber());

        return LectureMapper.lectureToLectureDto(lecture);
    }

    @Override
    public boolean deleteLecture(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElse(null);
        if (lecture == null) return false;

        checkPermission(lecture.getCourseUnitId(), lecture.getGroupNumber());

        lectureRepository.deleteById(lectureId);

        return true;
    }

    @Override
    public boolean updateLecture(UUID lectureId, LectureUpdateDto lectureUpdateDto) {
        Lecture lecture = lectureRepository.findById(lectureId).orElse(null);
        if (lecture == null) return false;

        checkPermission(lecture.getCourseUnitId(), lecture.getGroupNumber());

        Map<String, AttendanceDto> attendanceByStudentId =
                lectureUpdateDto.getAttendanceList()
                        .stream()
                        .collect(Collectors.toMap(
                                AttendanceDto::getStudentId,
                                Function.identity()
                        ));

        for (Attendance attendance : lecture.getAttendanceList()) {
            AttendanceDto dto = attendanceByStudentId.get(attendance.getStudentId());
            if (dto != null && dto.getStatus() != null) {
                attendance.setStatus(dto.getStatus());
            }
        }

        lectureRepository.save(lecture);

        return true;
    }
}

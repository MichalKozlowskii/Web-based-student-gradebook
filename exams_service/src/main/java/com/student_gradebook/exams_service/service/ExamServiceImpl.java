package com.student_gradebook.exams_service.service;

import com.student_gradebook.exams_service.controller.exceptions.EntityNotFoundException;
import com.student_gradebook.exams_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.exams_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.exams_service.dto.ExamDto;
import com.student_gradebook.exams_service.dto.ExamGradeCreationDto;
import com.student_gradebook.exams_service.dto.ScanResultDto;
import com.student_gradebook.exams_service.entity.Exam;
import com.student_gradebook.exams_service.mappers.ExamMapper;
import com.student_gradebook.exams_service.repository.ExamRepository;
import com.student_gradebook.exams_service.service.client.ExamsFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final ExamsFeignClient examsFeignClient;

    @Override
    public UUID createExam(ExamDto examDto, String lecturerId) {
        Exam exam = ExamMapper.examDtoToExam(examDto);
        exam.setLecturerId(lecturerId);

        return examRepository.save(exam).getId();
    }

    @Override
    public ExamDto fetchExam(UUID examId, String lecturerId) {
        Exam exam = examRepository.findById(examId).orElseThrow(
                () -> new EntityNotFoundException("Exam not found!"));
        if (!exam.getLecturerId().equals(lecturerId)) throw new UnAuthorizedActionException("You are not permitted!");

        return ExamMapper.examToExamDto(exam);
    }

    @Override
    public List<ExamDto> fetchExams(String lecturerId) {
        List<Exam> exams = examRepository.findAllByLecturerIdOrderByLastUpdatedDesc(lecturerId);

        return exams.stream()
                .map(ExamMapper::examToExamDto)
                .toList();
    }

    @Override
    public void updateExam(UUID examId, ExamDto examDto, String lecturerId) {
        Exam existing = examRepository.findById(examId).orElseThrow(
                () -> new EntityNotFoundException("Exam not found!"));
        if (!existing.getLecturerId().equals(lecturerId)) throw new UnAuthorizedActionException("You are not permitted!");

        existing.setTitle(examDto.getTitle());
        existing.setNumberOfTasks(examDto.getNumberOfTasks());
        existing.setScope(examDto.getScope());
        existing.setCourseUnitId(examDto.getCourseUnitId());
        existing.setWeight(examDto.getWeight());

        examRepository.save(existing);
    }

    @Override
    public void deleteExam(UUID examId, String lecturerId) {
        Exam existing = examRepository.findById(examId).orElseThrow(
                () -> new EntityNotFoundException("Exam not found!"));
        if (!existing.getLecturerId().equals(lecturerId)) throw new UnAuthorizedActionException("You are not permitted!");

        examRepository.deleteById(examId);
    }

    @Override
    public void gradeExam(UUID examId, String lecturerId, ScanResultDto scanResultDto) {
        Exam exam = examRepository.findById(examId).orElseThrow(
                () -> new EntityNotFoundException("Exam not found!"));
        if (!exam.getLecturerId().equals(lecturerId))
            throw new UnAuthorizedActionException("You are not permitted!");

        ExamGradeCreationDto examGradeCreationDto = ExamGradeCreationDto.builder()
                .title(exam.getTitle())
                .grade(scanResultDto.getGrade())
                .courseUnitId(exam.getCourseUnitId())
                .studentNumber(scanResultDto.getStudentNumber())
                .weight(exam.getWeight())
                .build();

        examsFeignClient.addGradeFromExam(examGradeCreationDto);
    }
}

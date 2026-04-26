package com.student_gradebook.grades_service.service;

import com.student_gradebook.grades_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.grades_service.controller.exceptions.ResourceNotFoundException;
import com.student_gradebook.grades_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.grades_service.dto.ExamGradeCreationDto;
import com.student_gradebook.grades_service.dto.GradeCreationDto;
import com.student_gradebook.grades_service.dto.GradeEditionDto;
import com.student_gradebook.grades_service.dto.GradeViewDto;
import com.student_gradebook.grades_service.dto.gradesList.GradeListDto;
import com.student_gradebook.grades_service.dto.gradesList.GradeListItem;
import com.student_gradebook.grades_service.entity.Grade;
import com.student_gradebook.grades_service.entity.Term;
import com.student_gradebook.grades_service.mappers.GradeMapper;
import com.student_gradebook.grades_service.repository.GradeRepository;
import com.student_gradebook.grades_service.service.client.GradesFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;
    private final TermService termService;
    private final GradesFeignClient gradesFeignClient;

    private void checkPermissionToGrade(String courseUnitId, String studentId) {
        Boolean canGrade = gradesFeignClient.canGrade(courseUnitId, studentId);
        if (canGrade == null) {
            throw new NoResponseFromApiException("Couldn't get response from groups microservice.");
        }

        if (!canGrade) {
            throw new UnAuthorizedActionException("This grade don't exist or can't be changed by you.");
        }
    }

    private List<String> fetchStudentIdsFromApi(String courseUnitId, Integer groupNumber) {
        List<String> studentIds = gradesFeignClient.fetchStudentIds(courseUnitId, groupNumber);
        if (studentIds == null) {
            throw new NoResponseFromApiException("Couldn't get response from groups microservice.");
        }

        if (studentIds.isEmpty()) {
            throw new UnAuthorizedActionException("You don't teach this group or group doesn't exist.");
        }

        return studentIds;
    }

    @Override
    public void addGrade(GradeCreationDto gradeCreationDto) {
        checkPermissionToGrade(gradeCreationDto.getCourseUnitId(), gradeCreationDto.getStudentId());

        Grade newGrade = GradeMapper.gradeCreationDtoToGrade(gradeCreationDto);
        newGrade.setTermId(termService.findActiveTerm().getId());

        gradeRepository.save(newGrade);
    }

    @Override
    public void addGradeFromExam(ExamGradeCreationDto examGradeCreationDto) {
        String studentId = gradesFeignClient.canGradeByStudentNumber(
                examGradeCreationDto.getCourseUnitId(), examGradeCreationDto.getStudentNumber());

        if (studentId == null)
            throw new UnAuthorizedActionException("This student don't exist or can't be graded by you.");

        Grade newGrade = Grade.builder()
                .title(examGradeCreationDto.getTitle())
                .grade(examGradeCreationDto.getGrade())
                .weight(examGradeCreationDto.getWeight())
                .courseUnitId(examGradeCreationDto.getCourseUnitId())
                .studentId(studentId)
                .termId(termService.findActiveTerm().getId())
                .build();

        gradeRepository.save(newGrade);
    }

    @Override
    public Boolean addGradesFromList(GradeListDto gradeListDto) {
        List<String> studentIds = fetchStudentIdsFromApi(gradeListDto.getCourseUnitId(), gradeListDto.getGroupNumber());

        Term activeTerm = termService.findActiveTerm();
        Set<Grade> grades = new HashSet<>();

        for (GradeListItem element : gradeListDto.getList()) {
            if (!studentIds.contains(element.getStudentId())) continue;

            Grade grade = Grade.builder()
                    .title(gradeListDto.getTitle())
                    .studentId(element.getStudentId())
                    .grade(element.getGrade())
                    .weight(gradeListDto.getWeight())
                    .termId(activeTerm.getId())
                    .courseUnitId(gradeListDto.getCourseUnitId())
                    .build();

            grades.add(grade);
        }

        if (grades.isEmpty()) return false;

        gradeRepository.saveAll(grades);
        return true;
    }

    @Override
    public List<GradeViewDto> fetchStudentGrades(String studentId, String courseUnitId) {
        List<Grade> grades = gradeRepository.findAllByCourseUnitIdAndStudentIdAndTermId(courseUnitId, studentId,
                termService.findActiveTerm().getId());

        return grades.stream()
                .map(GradeMapper::GradeToGradeViewDto)
                .toList();
    }

    @Override
    public List<GradeViewDto> fetchLastStudentGrades(String studentId) {
        List<Grade> grades = gradeRepository.findTop5ByStudentIdAndTermIdOrderByLastUpdatedDesc(studentId,
                termService.findActiveTerm().getId());

        return grades.stream()
                .map(GradeMapper::GradeToGradeViewDto)
                .toList();
    }

    @Override
    public Map<String, List<GradeViewDto>> fetchGradesInGroup(String courseUnitId, Integer groupNumber) {
        List<String> studentIds = fetchStudentIdsFromApi(courseUnitId, groupNumber);

        Map<String, List<GradeViewDto>> gradesMap = new HashMap<>();
        Term activeTerm = termService.findActiveTerm();

        for (String id : studentIds) {
            List<Grade> grades = gradeRepository
                    .findAllByCourseUnitIdAndStudentIdAndTermId(courseUnitId, id, activeTerm.getId());

            gradesMap.put(id, grades.stream().map(GradeMapper::GradeToGradeViewDto).toList());
        }

        return gradesMap;
    }

    @Override
    public void updateGrade(UUID gradeId, GradeEditionDto gradeEditionDto) {
        Grade existing = gradeRepository.findById(gradeId).orElseThrow(() ->
                new ResourceNotFoundException("Grade not found"));

        checkPermissionToGrade(existing.getCourseUnitId(), existing.getStudentId());

        if (gradeEditionDto.getGrade() != null) {
            existing.setGrade(gradeEditionDto.getGrade());
        }
        if (gradeEditionDto.getTitle() != null && !gradeEditionDto.getTitle().isBlank()) {
            existing.setTitle(gradeEditionDto.getTitle());
        }
        if (gradeEditionDto.getWeight() != null) {
            existing.setWeight(gradeEditionDto.getWeight());
        }

        gradeRepository.save(existing);
    }

    @Override
    public void deleteGrade(UUID gradeId) {
        Grade existing = gradeRepository.findById(gradeId).orElseThrow(() ->
                new ResourceNotFoundException("Grade not found"));

        checkPermissionToGrade(existing.getCourseUnitId(), existing.getStudentId());

        gradeRepository.delete(existing);
    }
}

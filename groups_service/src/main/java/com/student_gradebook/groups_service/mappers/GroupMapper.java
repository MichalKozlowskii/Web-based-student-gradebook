package com.student_gradebook.groups_service.mappers;

import com.student_gradebook.groups_service.dto.GroupDetailsDto;
import com.student_gradebook.groups_service.dto.GroupPreviewDto;
import com.student_gradebook.groups_service.entity.Group;

public class GroupMapper {
    public static GroupDetailsDto mapToGroupDetailsDto(Group group) {
        return GroupDetailsDto.builder()
                .id(group.getId())
                .groupNumber(group.getGroupNumber())
                .courseName(group.getCourseName())
                .courseUnitId(group.getCourseUnitId())
                .lecturerId(group.getLecturerId())
                .termId(group.getTerm().getId())
                .participants(group.getParticipants().stream().map(StudentMapper::mapToStudentDto).toList())
                .build();
    }

    public static GroupPreviewDto mapToGroupPreviewDto(Group group) {
        return GroupPreviewDto.builder()
                .id(group.getId())
                .groupNumber(group.getGroupNumber())
                .courseUnitId(group.getCourseUnitId())
                .courseName(group.getCourseName())
                .build();
    }
}

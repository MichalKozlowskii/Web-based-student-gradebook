package com.student_gradebook.groups_service.service;

import com.student_gradebook.groups_service.entity.Group;
import com.student_gradebook.groups_service.entity.Student;
import com.student_gradebook.groups_service.entity.Term;
import com.student_gradebook.groups_service.records.*;
import com.student_gradebook.groups_service.repository.GroupRepository;
import com.student_gradebook.groups_service.service.client.UsosApiFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupsServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private TermService termService;

    @Mock
    private UsosApiFeignClient feignClient;

    @InjectMocks
    private GroupsServiceImpl groupsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateGroups_savesNewGroup() {
        String lecturerId = "lect1";
        Term activeTerm = Term.builder().id("t1").build();
        when(termService.findActiveTerm()).thenReturn(activeTerm);

        ParticipantRecord participant = new ParticipantRecord("s1", "John", "Doe", "S123");
        Student student = Student.builder().id("s1").build();
        when(studentService.findOrSaveParticipants(any())).thenReturn(List.of(student));

        GroupRecord groupRecord = new GroupRecord("cu1", 1, "courseId1", new LangDictObject("Course","Course"), List.of(participant));
        GroupsResponse response = new GroupsResponse(Map.of("t1", List.of(groupRecord)));
        when(feignClient.fetchGroups()).thenReturn(response);

        when(groupRepository.findByCourseUnitIdAndGroupNumberAndTermAndLecturerId(any(), anyInt(), any(), any()))
                .thenReturn(Optional.empty());

        groupsService.updateGroups(lecturerId);

        verify(groupRepository).save(argThat(g -> g.getCourseUnitId().equals("cu1") && g.getParticipants().contains(student)));
    }

    @Test
    void updateGroups_updatesParticipantsIfDifferent() {
        String lecturerId = "lect1";
        Term activeTerm = Term.builder().id("t1").build();
        when(termService.findActiveTerm()).thenReturn(activeTerm);

        ParticipantRecord participant = new ParticipantRecord("s2", "Alice", "Smith", "S456");
        Student student = Student.builder().id("s2").build();
        when(studentService.findOrSaveParticipants(any())).thenReturn(List.of(student));

        GroupRecord groupRecord = new GroupRecord("cu2", 1, "courseId2", new LangDictObject("Course","Course"), List.of(participant));
        GroupsResponse response = new GroupsResponse(Map.of("t1", List.of(groupRecord)));
        when(feignClient.fetchGroups()).thenReturn(response);

        // existing group with DIFFERENT participants (to trigger update)
        Group existingGroup = Group.builder()
                .participants(List.of(Student.builder().id("other").build())) // different
                .groupNumber(1)
                .courseUnitId("cu2")
                .term(activeTerm)
                .lecturerId(lecturerId)
                .build();

        when(groupRepository.findByCourseUnitIdAndGroupNumberAndTermAndLecturerId("cu2", 1, activeTerm, lecturerId))
                .thenReturn(Optional.of(existingGroup));

        groupsService.updateGroups(lecturerId);

        // participants should now match
        assertEquals(List.of(student), existingGroup.getParticipants());
        verify(groupRepository).save(existingGroup);
    }


    @Test
    void fetchLecturerGroups_returnsMappedGroups() {
        String lecturerId = "lect1";
        Term term = Term.builder().id("t1").build();
        when(termService.findActiveTerm()).thenReturn(term);

        Group group = Group.builder().id(UUID.randomUUID()).lecturerId(lecturerId).term(term).build();
        when(groupRepository.findAllByLecturerIdAndTermId(lecturerId, "t1")).thenReturn(List.of(group));

        var result = groupsService.fetchLecturerGroups(lecturerId);
        assertEquals(1, result.size());
    }

    @Test
    void fetchStudentGroups_returnsMappedGroups() {
        String studentId = "s1";
        Term term = Term.builder().id("t1").build();
        when(termService.findActiveTerm()).thenReturn(term);

        Group group = Group.builder().id(UUID.randomUUID()).participants(List.of()).term(term).build();
        when(groupRepository.findAllByParticipantsIdAndTermId(studentId, "t1")).thenReturn(List.of(group));

        var result = groupsService.fetchStudentGroups(studentId);
        assertEquals(1, result.size());
    }

    @Test
    void fetchGroupDetails_returnsMappedDto() {
        UUID groupId = UUID.randomUUID();
        String userId = "user1";

        Term term = Term.builder().id("t1").build();
        Student student = Student.builder().id("s1").build();
        List<Student> participants = List.of(student);

        Group group = Group.builder()
                .id(groupId)
                .term(term)
                .participants(participants)
                .courseName("Course Name")
                .courseUnitId("CU1")
                .groupNumber(1)
                .lecturerId("lect1")
                .build();

        when(groupRepository.findAccessibleGroupById(groupId, userId)).thenReturn(Optional.of(group));

        var result = groupsService.fetchGroupDetails(groupId, userId);

        assertTrue(result.isPresent());
        assertEquals(groupId, result.get().getId());
        assertEquals("t1", result.get().getTermId());
        assertEquals(1, result.get().getGroupNumber());
        assertEquals(1, result.get().getParticipants().size());
        assertEquals("s1", result.get().getParticipants().get(0).getId());
    }


    @Test
    void canGrade_returnsFalseIfStudentNotFound() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("id")).thenReturn("lect1");
        when(studentService.findStudentById("s1")).thenReturn(null);

        Boolean result = groupsService.canGradeByStudentId(jwt, "cu1", "s1");
        assertFalse(result);
    }

    @Test
    void canGrade_returnsTrueIfExists() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("id")).thenReturn("lect1");

        Student student = Student.builder().id("s1").build();
        when(studentService.findStudentById("s1")).thenReturn(student);
        Term term = Term.builder().id("t1").build();
        when(termService.findActiveTerm()).thenReturn(term);

        when(groupRepository.existsByCourseUnitIdAndLecturerIdAndTermAndParticipantsContaining("cu1","lect1",term,student))
                .thenReturn(true);

        Boolean result = groupsService.canGradeByStudentId(jwt, "cu1", "s1");
        assertTrue(result);
    }

    @Test
    void fetchStudentIds_returnsList() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("id")).thenReturn("lect1");

        Term term = Term.builder().id("t1").build();
        when(termService.findActiveTerm()).thenReturn(term);

        when(groupRepository.findParticipantIds("cu1",1,"lect1",term)).thenReturn(List.of("s1","s2"));

        List<String> result = groupsService.fetchStudentIds(jwt, "cu1", 1);
        assertEquals(2, result.size());
    }

    @Test
    void isAuthorisedInGroup_returnsTrue() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("id")).thenReturn("lect1");

        Term term = Term.builder().id("t1").build();
        when(termService.findActiveTerm()).thenReturn(term);

        when(groupRepository.existsByCourseUnitIdAndGroupNumberAndLecturerIdAndTerm("cu1",1,"lect1",term))
                .thenReturn(true);

        Boolean result = groupsService.isAuthorisedInGroup(jwt, "cu1",1);
        assertTrue(result);
    }
}

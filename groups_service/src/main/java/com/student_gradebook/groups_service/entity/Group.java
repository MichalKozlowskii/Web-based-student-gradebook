package com.student_gradebook.groups_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "student_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "course_unit_id", length = 10)
    private String courseUnitId;

    @Column(name = "group_number")
    private Integer groupNumber;

    @Column(name = "course_name", length = 80)
    private String courseName;

    @ManyToOne
    private Term term;

    @ManyToMany
    private List<Student> participants;

    @Column(name = "lecturer_id", length = 10)
    private String lecturerId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}

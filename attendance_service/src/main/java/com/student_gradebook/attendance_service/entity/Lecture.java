package com.student_gradebook.attendance_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "lectures")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "course_unit_id", length = 10)
    private String courseUnitId;

    @Column(name = "group_number")
    private Integer groupNumber;

    @Column(name = "term_id", length = 10)
    private String termId;

    @OneToMany(
            mappedBy = "lecture",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Attendance> attendanceList;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}

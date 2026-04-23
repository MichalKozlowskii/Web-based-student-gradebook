package com.student_gradebook.grades_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "grades")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "student_id", length = 10)
    private String studentId;

    @Column(name = "course_unit_id", length = 10)
    private String courseUnitId;

    @Column(name = "term_id", length = 10)
    private String termId;

    @Column(name = "title", length = 30)
    private String title;

    @Column(name = "grade", length = 5)
    private String grade;

    @Column(name = "weight")
    @Size(min = 1, max = 10)
    private Integer weight = 1;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}

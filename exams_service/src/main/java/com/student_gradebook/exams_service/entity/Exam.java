package com.student_gradebook.exams_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.UUID;

@Entity
@Table(name = "exams")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "title", length = 30)
    private String title;

    @Column(name = "number_of_tasks")
    private Integer numberOfTasks;

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private TreeMap<Integer, Double> scope = new TreeMap<>();

    @Column(name = "course_unit_id", length = 10)
    private String courseUnitId;

    @Column(name = "lecturer_id", length = 10)
    private String lecturerId;

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

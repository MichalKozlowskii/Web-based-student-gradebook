package com.student_gradebook.attendance_service.entity;

import com.student_gradebook.attendance_service.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "student_id", length = 10)
    private String studentId;

    @Column(name = "status", length = 15)
    private Status status;

    @ManyToOne(optional = false)
    private Lecture lecture;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}

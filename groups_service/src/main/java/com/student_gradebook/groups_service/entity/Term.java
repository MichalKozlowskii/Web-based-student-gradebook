package com.student_gradebook.groups_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "terms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Term {
    @Id
    @Column(length = 10)
    private String id;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}

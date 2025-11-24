package com.pbl.elearning.enrollment.models;

import javax.persistence.*;

import com.pbl.elearning.enrollment.enums.AssignmentStatus;
import com.pbl.elearning.course.domain.Course;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.*;
@Entity
@Table(name = "assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;
    private String title;
    private String description;
    private OffsetDateTime dueDate;
    @Builder.Default
    private Integer maxScore = 100;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    private OffsetDateTime createdAt;
}
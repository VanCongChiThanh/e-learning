package com.pbl.elearning.enrollment.models;

import javax.persistence.*;

import com.pbl.elearning.enrollment.Enum.AssignmentStatus;
import lombok.*;
import java.time.Instant;
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

    private UUID courseId;
    private String title;
    private String description;
    private OffsetDateTime dueDate;
    @Builder.Default
    private Integer maxScore = 100;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    private OffsetDateTime createdAt;
}

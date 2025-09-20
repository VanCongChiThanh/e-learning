package com.pbl.elearning.enrollment.models;

import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "course_id"})
})
public class Enrollment {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    private OffsetDateTime enrollmentDate;
    private OffsetDateTime completionDate;

    @Column(precision = 5, scale = 2)
    private Double progressPercentage;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private Integer totalWatchTimeMinutes;
    private OffsetDateTime lastAccessedAt; // ko can
    private OffsetDateTime createdAt; // ko can
    private OffsetDateTime updatedAt; // ko can
}

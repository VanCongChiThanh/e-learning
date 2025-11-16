package com.pbl.elearning.enrollment.models;

import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.course.domain.Course;
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
        @UniqueConstraint(columnNames = {"userId", "courseId"})
})
public class Enrollment {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "userId", 
        referencedColumnName = "user_id", 
        nullable = false
    )
    private UserInfo user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    private OffsetDateTime enrollmentDate;
    private OffsetDateTime completionDate;

    @Column(precision = 5, scale = 2)
    private Double progressPercentage;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private Integer totalWatchTimeMinutes;
    private OffsetDateTime lastAccessedAt;
}

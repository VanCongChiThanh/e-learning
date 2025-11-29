package com.pbl.elearning.enrollment.models;

import com.pbl.elearning.course.domain.Lecture;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"enrollmentId", "lectureId"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Progress {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "enrollmentId", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lectureId", nullable = false)
    private Lecture lecture;

    private Boolean isCompleted;
    private LocalTime lastViewedAt;
    private OffsetDateTime completionDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

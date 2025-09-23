package com.pbl.elearning.enrollment.models;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id", "lecture_id"})
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
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "lecture_id", nullable = false)
    private UUID lectureId;

    private Boolean isCompleted;
    private Integer watchTimeMinutes;
    private OffsetDateTime completionDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

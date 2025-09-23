package com.pbl.elearning.enrollment.models;

import javax.persistence.*;

import com.pbl.elearning.enrollment.Enum.SubmissionStatus;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;
@Entity
@Table(name = "assignment_submissions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentSubmission {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    private String submissionText;
    private UUID fileId;
    private Integer score;
    private String feedback;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    private OffsetDateTime submittedAt;
    private OffsetDateTime gradedAt;
    private UUID gradedBy;
}

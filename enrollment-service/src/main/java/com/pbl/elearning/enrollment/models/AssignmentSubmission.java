package com.pbl.elearning.enrollment.models;

import javax.persistence.*;

import com.pbl.elearning.enrollment.Enum.SubmissionStatus;
import com.pbl.elearning.security.domain.User;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignmentSubmissions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentSubmission {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignmentId", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollmentId", nullable = false)
    private Enrollment enrollment;

    private String submissionText;
    private UUID fileId;
    private Integer score;
    private String feedback;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    private OffsetDateTime submittedAt;
    private OffsetDateTime gradedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gradedBy")
    private User gradedBy;
}

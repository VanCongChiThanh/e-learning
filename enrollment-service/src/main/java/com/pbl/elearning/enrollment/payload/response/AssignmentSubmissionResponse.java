package com.pbl.elearning.enrollment.payload.response;

import com.pbl.elearning.enrollment.Enum.SubmissionStatus;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmissionResponse {
    private UUID id;
    private UUID assignmentId;
    private UUID userId;
    private UUID enrollmentId;
    private String submissionText;
    private UUID fileId;
    private Integer score;
    private String feedback;
    private SubmissionStatus status;
    private OffsetDateTime submittedAt;
    private OffsetDateTime gradedAt;
    private UUID gradedBy;
}
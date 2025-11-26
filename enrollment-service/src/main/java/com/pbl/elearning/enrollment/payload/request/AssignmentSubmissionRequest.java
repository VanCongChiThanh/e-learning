package com.pbl.elearning.enrollment.payload.request;

import com.pbl.elearning.enrollment.enums.SubmissionStatus;
import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmissionRequest {
    private UUID assignmentId;
    private UUID userId;
    private UUID enrollmentId;
    private String submissionText;
    private UUID fileId;
    private Integer score;
    private String feedback;
    private SubmissionStatus status; // optional, có thể để null nếu chỉ submit
}
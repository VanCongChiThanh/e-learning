package com.pbl.elearning.enrollment.payload.response;

import com.pbl.elearning.enrollment.models.CodeSubmission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CodeSubmissionResponse {
    private UUID codeSubmissionId;
    private UUID userId;
    private UUID exerciseId;
    private String sourceCode;
    private Integer languageId;
    private String status;
    private Double executionTime;
    private Integer memoryUsage;
    private String stdout;
    private String stderr;
    private String geminiFeedback; // Trả về dạng String JSON, FE sẽ tự parse
    private Integer pointsAchieved;
    private LocalDateTime submittedAt;

    public static CodeSubmissionResponse fromEntity(CodeSubmission submission) {
        return CodeSubmissionResponse.builder()
                .codeSubmissionId(submission.getCodeSubmissionId())
                .userId(submission.getUserId())
                .exerciseId(submission.getExerciseId())
                .sourceCode(submission.getSourceCode())
                .languageId(submission.getLanguageId())
                .status(submission.getStatus())
                .executionTime(submission.getExecutionTime())
                .memoryUsage(submission.getMemoryUsage())
                .stdout(submission.getStdout())
                .stderr(submission.getStderr())
                .geminiFeedback(submission.getGeminiFeedback())
                .pointsAchieved(submission.getPointsAchieved())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
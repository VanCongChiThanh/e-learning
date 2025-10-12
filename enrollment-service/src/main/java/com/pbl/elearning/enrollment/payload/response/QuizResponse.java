package com.pbl.elearning.enrollment.payload.response;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private UUID id;
    private UUID lectureId;
    private String title;
    private String description;
    private Integer timeLimitMinutes;
    private Integer passingScore;
    private Integer maxAttempts;
    private Integer numberQuestions;
    private Boolean isActive;
    private java.time.OffsetDateTime createdAt;
}
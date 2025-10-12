package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {
    private UUID lectureId;
    private String title;
    private String description;
    private Integer timeLimitMinutes;
    private Integer passingScore;
    private Integer maxAttempts;
    private Integer numberQuestions;
}

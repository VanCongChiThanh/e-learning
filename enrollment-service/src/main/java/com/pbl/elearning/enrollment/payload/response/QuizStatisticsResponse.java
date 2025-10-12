package com.pbl.elearning.enrollment.payload.response;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizStatisticsResponse {
    private UUID userId;
    private UUID courseId;
    private Integer totalQuizzes;
    private Integer completedQuizzes;
    private Integer passedQuizzes;
    private Double averageScore;
    private Double completionRate;
    private Double passRate;
    private Integer totalAttempts;
}
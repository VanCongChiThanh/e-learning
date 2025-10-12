package com.pbl.elearning.enrollment.payload.response;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionResponse {
    private UUID id;
    private UUID quizId;
    private String quizTitle;
    private UUID userId;
    private String userEmail;
    private Integer attemptNumber;
    private Integer totalScore;
    private Integer maxPossibleScore;
    private Double scorePercentage;
    private Boolean isPassed;
    private OffsetDateTime startedAt;
    private OffsetDateTime submittedAt;
    private Integer timeTakenMinutes;
    private Boolean isCompleted;
    private List<QuizAnswerResponse> answers;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAnswerResponse {
        private UUID questionId;
        private String questionText;
        private List<String> options;
        private Integer selectedAnswerIndex;
        private Integer correctAnswerIndex;
        private Boolean isCorrect;
        private Integer pointsEarned;
        private Integer maxPoints;
    }
}
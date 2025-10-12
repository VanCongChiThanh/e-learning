package com.pbl.elearning.enrollment.payload.request;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionRequest {
    private UUID quizId;
    private UUID userId;
    private UUID enrollmentId; 
    private List<QuizAnswerRequest> answers;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAnswerRequest {
        private UUID questionId;
        private Integer selectedAnswerIndex;
    }
}
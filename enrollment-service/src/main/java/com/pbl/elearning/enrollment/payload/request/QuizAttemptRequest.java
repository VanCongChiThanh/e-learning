package com.pbl.elearning.enrollment.payload.request;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptRequest {
    private UUID quizId;
    private UUID userId;
    private UUID enrollmentId;
    private UUID questionId;
    private String selectedOption;
    private Integer pointsEarned;
    private Integer attemptNumber;
    private Integer timeTakenMinutes;
}
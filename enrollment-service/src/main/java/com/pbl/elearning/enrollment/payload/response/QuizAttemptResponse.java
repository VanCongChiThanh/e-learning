package com.pbl.elearning.enrollment.payload.response;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptResponse {
    private UUID id;
    private UUID quizId;
    private UUID userId;
    private UUID enrollmentId;
    private UUID questionId;
    private String selectedOption;
    private Boolean isCorrect;
    private Integer pointsEarned;
    private Integer attemptNumber;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private Integer timeTakenMinutes;
}

package com.pbl.elearning.enrollment.payload.response;

import com.pbl.elearning.enrollment.Enum.QuestionType;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionAnswerResponse {
    private UUID id;
    private UUID quizId;
    private String questionText;
    private QuestionType questionType;
    private String answerText;
    private Boolean isCorrect;
    private Integer points;
    private Integer sortOrder;
    private java.time.OffsetDateTime createdAt;
}

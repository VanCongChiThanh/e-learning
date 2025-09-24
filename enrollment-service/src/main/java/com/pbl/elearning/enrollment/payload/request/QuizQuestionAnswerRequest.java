package com.pbl.elearning.enrollment.payload.request;

import com.pbl.elearning.enrollment.Enum.QuestionType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionAnswerRequest {
    private String questionText;
    private QuestionType questionType;
    private String answerText;
    private Boolean isCorrect;
    private Integer points;
    private Integer sortOrder;
}
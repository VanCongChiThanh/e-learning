package com.pbl.elearning.enrollment.payload.request;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkQuizQuestionsRequest {
    private UUID quizId;
    private List<QuizQuestionAnswerRequest> questions;
}
package com.pbl.elearning.enrollment.payload.request;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionAnswerRequest {
    private UUID quizId;
    private String questionText;
    
    private List<String> options;
    
    private Integer correctAnswerIndex;
    
    private Integer points;
    private Integer sortOrder;
}

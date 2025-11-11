package com.pbl.elearning.enrollment.payload.response;

import lombok.*;
import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionAnswerResponse {
    private UUID id;
    // private UUID quizId;
    private String questionText;
    private List<String> options;
    private Integer correctAnswerIndex;
    private Integer points;
    private Integer sortOrder;
    private OffsetDateTime createdAt;
}

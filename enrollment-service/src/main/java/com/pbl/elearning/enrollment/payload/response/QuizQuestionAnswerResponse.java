package com.pbl.elearning.enrollment.payload.response;

import lombok.*;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionAnswerResponse {
    private UUID id;
    private UUID quizId;
    private String questionText;

    // Các lựa chọn
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    // Đáp án đúng
    private String correctAnswer;

    private Integer points;
    private Integer sortOrder;
    private OffsetDateTime createdAt;
}

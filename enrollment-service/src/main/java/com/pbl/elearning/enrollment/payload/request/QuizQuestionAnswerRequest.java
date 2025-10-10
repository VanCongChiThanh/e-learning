package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionAnswerRequest {
    private String questionText;

    // Các lựa chọn A–D
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    // Đáp án đúng: 'A', 'B', 'C', hoặc 'D'
    private String correctAnswer;

    private Integer points;
    private Integer sortOrder;
}

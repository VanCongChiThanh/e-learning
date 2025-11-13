package com.pbl.elearning.enrollment.payload.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateRequest {
    private String questionText;
    private List<String> options;

    private Integer correctAnswerIndex; 

    private Integer points = 10; 
    private Integer sortOrder;
}

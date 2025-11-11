package com.pbl.elearning.enrollment.payload.request;

import com.pbl.elearning.enrollment.payload.request.QuestionCreateRequest;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizCreateRequestDTO {
    @NotNull
    private UUID lectureId; 
    private String title;
    private String description;

    private Integer timeLimitMinutes;
    
    private Integer passingScore; 
    
    private Integer maxAttempts; 
    
    private List<QuestionCreateRequest> questions;
}

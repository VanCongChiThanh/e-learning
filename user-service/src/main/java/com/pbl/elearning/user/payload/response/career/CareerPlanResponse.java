package com.pbl.elearning.user.payload.response.career;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.domain.CareerPlan;
import com.pbl.elearning.user.payload.dto.CareerPlanSectionDTO;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CareerPlanResponse {

    private UUID planId;

    private UUID userId;

    private String role;      // Software Engineer, Backend Developer,...

    private String goal;      // Become senior backend developer,...

    private List<CareerPlanSectionDTO> sections; // các section của lộ trình

    private Map<String, Object> answers; // các câu trả lời cho bộ câu hỏi định hướng nghề nghiệp

    private Double overallProgress; // % tiến độ tổng thực hiện lộ trình

    public static CareerPlanResponse toResponse(CareerPlan careerPlan,String overallProgress) {
        return CareerPlanResponse.builder()
                .planId(careerPlan.getId())
                .userId(careerPlan.getUserId())
                .role(careerPlan.getRole())
                .goal(careerPlan.getGoal())
                .answers(careerPlan.getAnswers())
                .sections(careerPlan.getSections())
                .overallProgress(overallProgress != null ? Double.valueOf(overallProgress) : 0.0)
                .build();
    }
}
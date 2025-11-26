package com.pbl.elearning.user.payload.response.career;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.payload.dto.CareerPlanSectionDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CareerPlanRequest {
    private String role;

    private String goal;

    private Map<String, Object> answers;

    private List<CareerPlanSectionDTO> sections;
}
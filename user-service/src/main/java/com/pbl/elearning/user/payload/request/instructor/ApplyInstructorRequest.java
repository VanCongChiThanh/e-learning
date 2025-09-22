package com.pbl.elearning.user.payload.request.instructor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApplyInstructorRequest {
    private String cvUrl;
    private String portfolioLink;
    private String motivation;
}
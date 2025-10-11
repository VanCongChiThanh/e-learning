package com.pbl.elearning.user.payload.request.instructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApplyInstructorRequest {
    @NotBlank
    private String cvUrl;
    private String portfolioLink;
    private String motivation;
    private JsonNode extraInfo;
}
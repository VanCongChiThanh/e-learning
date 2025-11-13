package com.pbl.elearning.user.payload.request.instructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApplyInstructorRequest {
    @NotBlank @URL private String cvUrl;

    @NotBlank @URL private String portfolioLink;

    private String motivation;

    private JsonNode extraInfo;
}
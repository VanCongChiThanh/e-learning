package com.pbl.elearning.user.payload.response.instructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.domain.InstructorApplication;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.payload.response.UserProfileResponse;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyInstructorResponse {
    private String cvUrl;
    private String portfolioLink;
    private String motivation;
    private String status;
    private JsonNode extraInfo;
    public static ApplyInstructorResponse toResponse(InstructorApplication application) {
        return ApplyInstructorResponse.builder()
                .cvUrl(application.getCvUrl())
                .portfolioLink(application.getPortfolioLink())
                .motivation(application.getMotivation())
                .status(application.getStatus().name())
                .extraInfo(application.getExtraInfo())
                .build();
    }
}
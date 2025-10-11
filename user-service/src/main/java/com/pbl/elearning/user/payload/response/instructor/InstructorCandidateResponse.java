package com.pbl.elearning.user.payload.response.instructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.domain.InstructorApplication;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorCandidateResponse {
    private UUID id;
    private UserInfoResponse userInfo;
    private String cvUrl;
    private String portfolioLink;
    private String motivation;
    private String status;
    public static InstructorCandidateResponse toResponse(UserInfoResponse userInfo, InstructorApplication application) {
        return InstructorCandidateResponse.builder()
                .userInfo(userInfo)
                .id(application.getId())
                .cvUrl(application.getCvUrl())
                .portfolioLink(application.getPortfolioLink())
                .motivation(application.getMotivation())
                .status(application.getStatus().name())
                .build();
    }
}
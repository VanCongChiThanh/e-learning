package com.pbl.elearning.user.payload.response.instructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorCandidateResponse {
    private UserInfoResponse userInfo;
    private String cvUrl;
    private String portfolioLink;
    private String motivation;
    private String status;
    public static InstructorCandidateResponse toResponse(UserInfoResponse userInfo, String cvUrl, String portfolioLink, String motivation, String status) {
        return InstructorCandidateResponse.builder()
                .userInfo(userInfo)
                .cvUrl(cvUrl)
                .portfolioLink(portfolioLink)
                .motivation(motivation)
                .status(status)
                .build();
    }
}
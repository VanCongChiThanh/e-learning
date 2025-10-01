package com.pbl.elearning.user.payload.response.instructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.domain.InstructorProfile;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorProfileResponse {
    private String bio;
    private String headline;
    private String biography;
    private String linkedin;
    private String github;
    private String facebook;
    private String youtube;
    private String personalWebsite;
    private UserInfoResponse userInfo;

    public static InstructorProfileResponse toResponse(InstructorProfile profile, UserInfoResponse userInfo) {
        return InstructorProfileResponse.builder()
                .bio(profile.getBio())
                .headline(profile.getHeadline())
                .biography(profile.getBiography())
                .linkedin(profile.getLinkedin())
                .github(profile.getGithub())
                .facebook(profile.getFacebook())
                .youtube(profile.getYoutube())
                .personalWebsite(profile.getPersonalWebsite())
                .userInfo(userInfo)
                .build();

    }

}
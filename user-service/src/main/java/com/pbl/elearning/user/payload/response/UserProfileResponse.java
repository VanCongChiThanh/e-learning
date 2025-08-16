package com.pbl.elearning.user.payload.response;

import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.domain.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String avatar;
    private String email;
    private Gender gender;
    private Boolean canPostReel;
    private Integer streakCount;
    public static UserProfileResponse toResponse(UserInfo userInfo) {
        return UserProfileResponse.builder()
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .avatar(userInfo.getAvatar())
                .userId(userInfo.getUserId())
                .email(userInfo.getEmail())
                .build();
    }
}
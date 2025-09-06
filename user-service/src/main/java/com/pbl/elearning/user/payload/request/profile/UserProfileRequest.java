package com.pbl.elearning.user.payload.request.profile;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.domain.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserProfileRequest {
    private String firstName;
    private String lastName;
    private String avatar;
    private Gender gender;
}
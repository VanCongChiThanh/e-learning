package com.pbl.elearning.user.payload.request.instructor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InstructorProfileRequest {
    private String bio;
    private String headline;
    private String biography;
    private String linkedin;
    private String github;
    private String facebook;
    private String youtube;
    private String personalWebsite;
}
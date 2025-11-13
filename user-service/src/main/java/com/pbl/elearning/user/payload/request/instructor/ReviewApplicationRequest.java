package com.pbl.elearning.user.payload.request.instructor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.domain.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReviewApplicationRequest {
    @NotNull(message = "status is required")
    private ApplicationStatus status;
    private String reason;
}
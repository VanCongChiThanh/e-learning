package com.pbl.elearning.user.payload.request.instructor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.user.domain.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReviewApplicationRequest {
    private ApplicationStatus status; // "APPROVED" or "REJECTED"
    private String reason;
}
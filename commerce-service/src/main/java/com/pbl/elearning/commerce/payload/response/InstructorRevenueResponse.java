package com.pbl.elearning.commerce.payload.response;

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
public class InstructorRevenueResponse {
    private UserInfoResponse instructor;
    private Integer totalCourses;
    private Double totalRevenue;
    private Double commissionPercentage;
    private Double netEarnings;
}
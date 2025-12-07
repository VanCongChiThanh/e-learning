package com.pbl.elearning.commerce.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorCourseRevenueResponse {
    private UUID courseId;
    private String title;
    private BigDecimal price;

    private Long totalSales;
    private BigDecimal grossRevenue;
    private BigDecimal netEarnings;
}
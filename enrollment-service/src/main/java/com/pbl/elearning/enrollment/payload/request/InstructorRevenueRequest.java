package com.pbl.elearning.enrollment.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InstructorRevenueRequest {

    @JsonProperty("start_date")
    private Long startDate;

    @JsonProperty("end_date")
    private Long endDate;

    @JsonIgnore
    public Instant getStartInstant() {
        return startDate != null ? Instant.ofEpochMilli(startDate) : null;
    }

    @JsonIgnore
    public Instant getEndInstant() {
        return endDate != null ? Instant.ofEpochMilli(endDate) : null;
    }
}
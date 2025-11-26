package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgressRequest {
    private Boolean isCompleted;       
    private LocalTime lastViewedAt; 
    private OffsetDateTime completionDate;
}
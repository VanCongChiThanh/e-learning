package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgressRequest {
    private Boolean isCompleted;       
    private Integer watchTimeMinutes; 
    private OffsetDateTime completionDate;
}
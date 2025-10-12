package com.pbl.elearning.enrollment.payload.request;

import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEnrollmentRequest {
    private Double progressPercentage;      
    private EnrollmentStatus status;         
    private OffsetDateTime completionDate;  
    private Integer totalWatchTimeMinutes;   
    private OffsetDateTime lastAccessedAt;   
}

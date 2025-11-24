package com.pbl.elearning.enrollment.payload.request;

import com.pbl.elearning.enrollment.enums.EnrollmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEnrollmentRequest {
    private Double progressPercentage;
    private EnrollmentStatus status;
    private LocalDateTime completionDate;
    private Integer totalWatchTimeMinutes;
    private LocalDateTime lastAccessedAt;
}
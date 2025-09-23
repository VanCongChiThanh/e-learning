package com.pbl.elearning.enrollment.payload.response;

import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private OffsetDateTime enrollmentDate;
    private OffsetDateTime completionDate;
    private Double progressPercentage;
    private EnrollmentStatus status;
    private Integer totalWatchTimeMinutes;
    private OffsetDateTime lastAccessedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
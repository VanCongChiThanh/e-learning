package com.pbl.elearning.enrollment.payload.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentProgressSummaryResponse {
    private UUID enrollmentId;
    private UUID userId;
    private UUID courseId;
    private String courseTitle;
    
    // Progress summary
    private Double progressPercentage;
    private Integer completedLecturesCount;
    private Integer totalLecturesCount;
    private Double totalWatchTimeMinutes;
    
    // Status info
    private String enrollmentStatus;
    private LocalDateTime enrollmentDate;
    private LocalDateTime completionDate;
    private LocalDateTime lastAccessedAt;
    
    // Recent activity
    private UUID lastViewedLectureId;
    private String lastViewedLectureTitle;
}
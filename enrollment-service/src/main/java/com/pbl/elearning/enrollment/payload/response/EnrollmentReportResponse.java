package com.pbl.elearning.enrollment.payload.response;

import lombok.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentReportResponse {
    private UUID enrollmentId;
    private UUID userId;
    private String userEmail;
    private String userFullName;
    private String avatar;
    private UUID courseId;
    private String courseTitle;
    private String enrollmentStatus;
    private Double progressPercentage;
    private Double totalWatchTimeMinutes;
    private LocalDateTime enrollmentDate;
    private LocalDateTime completionDate;
    private LocalDateTime lastAccessedAt;

    private Integer totalQuizzes;
    private Integer completedQuizzes;
    private Integer passedQuizzes;
    private Double averageQuizScore;

    private Integer totalAssignments;
    private Integer submittedAssignments;
    private Integer gradedAssignments;
    private Double averageAssignmentScore;

    private Boolean hasCertificate;
    private String certificateNumber;
    private OffsetDateTime certificateIssuedDate;
}
package com.pbl.elearning.enrollment.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentLearningResponse {
    
    // Enrollment info
    private UUID enrollmentId;
    private String enrollmentStatus;
    private Double enrollmentProgressPercentage;
    private OffsetDateTime lastAccessedAt;
    
    // Course info
    private UUID courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseImage;
    
    // Section info
    private UUID sectionId;
    private String sectionTitle;
    private Integer position;
    
    // Recent lecture info
    private UUID lectureId;
    private String lectureTitle;
    private String lectureVideoUrl;
    private Integer lectureDurationMinutes;
    private Integer lectureOrder;
    
    // Progress info
    private UUID progressId;
    private Boolean isLectureCompleted;
    private LocalTime lastViewedAt;
    private OffsetDateTime progressUpdatedAt;
    private Double lectureProgressPercentage;
    
    // Learning session info
    private String currentLearningStatus; // "CONTINUE_WATCHING", "START_NEW", "COMPLETED"
    private String recommendedAction; // "Continue from 05:30", "Start this lecture", "Move to next lecture"
}
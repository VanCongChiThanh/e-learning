package com.pbl.elearning.enrollment.payload.response;

import lombok.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureProgressUpdateResponse {
    private UUID progressId;
    private UUID enrollmentId;
    private UUID lectureId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime lastViewedAt;
    private Double lectureProgressPercentage;
    private Boolean isLectureCompleted;
    private OffsetDateTime updatedAt;
    
    // Enrollment progress info
    private Double enrollmentProgressPercentage;
    private Boolean isEnrollmentCompleted;
    private Integer completedLecturesCount;
    private Integer totalLecturesCount;
}
package com.pbl.elearning.enrollment.payload.response;

import com.pbl.elearning.course.payload.response.CourseResponeInstructor;
import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import com.pbl.elearning.user.payload.response.UserInfoResponse;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private UUID id;
    private UserInfoResponse user;
    private CourseResponeInstructor course;
    private OffsetDateTime enrollmentDate;
    private OffsetDateTime completionDate;
    private Double progressPercentage;
    private EnrollmentStatus status;
    private Integer totalWatchTimeMinutes;
    private OffsetDateTime lastAccessedAt;
}
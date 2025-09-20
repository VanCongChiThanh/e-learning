package com.pbl.elearning.enrollment.payload.request;

import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEnrollmentRequest {
    private Double progressPercentage;       // % tiến độ học tập
    private EnrollmentStatus status;         // Trạng thái: IN_PROGRESS, COMPLETED, DROPPED...
    private OffsetDateTime completionDate;   // Ngày hoàn thành (nếu có)
    private Integer totalWatchTimeMinutes;   // Tổng thời gian học (phút)
    private OffsetDateTime lastAccessedAt;   // Lần truy cập cuối
}

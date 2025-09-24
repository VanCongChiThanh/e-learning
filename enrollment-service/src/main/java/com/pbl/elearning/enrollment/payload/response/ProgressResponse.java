package com.pbl.elearning.enrollment.payload.response;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private UUID id;                   // id của progress
    private UUID enrollmentId;         // id enrollment liên kết
    private UUID lectureId;            // id bài giảng
    private Boolean isCompleted;       // đã hoàn thành chưa
    private Integer watchTimeMinutes;  // tổng số phút đã xem
    private OffsetDateTime completionDate; // thời điểm hoàn thành bài
    private OffsetDateTime createdAt;  // tạo lúc nào
    private OffsetDateTime updatedAt;  // cập nhật lúc nào
}
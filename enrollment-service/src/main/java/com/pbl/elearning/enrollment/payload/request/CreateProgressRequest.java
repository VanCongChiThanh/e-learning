package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProgressRequest {
    private UUID enrollmentId;      // enrollment đã tạo trước đó
    private UUID lectureId;         // lecture cụ thể
//    private Boolean isCompleted;    // mặc định false có thể không truyền
//    private Integer watchTimeMinutes; // số phút xem ban đầu, default 0
//    private OffsetDateTime completionDate; // optional, null nếu chưa hoàn thành
}
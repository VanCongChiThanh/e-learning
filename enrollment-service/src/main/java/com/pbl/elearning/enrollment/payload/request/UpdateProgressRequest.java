package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgressRequest {
    private Boolean isCompleted;       // đã hoàn thành bài chưa
    private Integer watchTimeMinutes;  // cập nhật số phút xem
    private OffsetDateTime completionDate; // set khi hoàn thành bài
}
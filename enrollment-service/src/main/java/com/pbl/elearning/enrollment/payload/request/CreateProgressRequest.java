package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProgressRequest {
    private UUID enrollmentId;
    private UUID lectureId;         
}
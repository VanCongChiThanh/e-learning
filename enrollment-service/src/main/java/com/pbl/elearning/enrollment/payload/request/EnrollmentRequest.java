package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    private UUID userId;
    private UUID courseId;
    private OffsetDateTime enrollmentDate;  // Có thể để null, backend tự set default
}

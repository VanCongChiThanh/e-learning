package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {
    private UUID courseId;
    private String title;
    private String description;
    private OffsetDateTime dueDate;
}

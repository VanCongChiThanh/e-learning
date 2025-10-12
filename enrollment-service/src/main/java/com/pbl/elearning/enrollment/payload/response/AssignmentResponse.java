package com.pbl.elearning.enrollment.payload.response;

import com.pbl.elearning.enrollment.Enum.AssignmentStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private OffsetDateTime dueDate;
    private AssignmentStatus status;
    private OffsetDateTime createdAt;
}
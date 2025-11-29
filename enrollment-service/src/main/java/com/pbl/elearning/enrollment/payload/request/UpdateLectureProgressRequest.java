package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import javax.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLectureProgressRequest {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Lecture ID is required")
    private UUID lectureId;
    
    @NotNull(message = "Watched seconds is required")
    private LocalTime lastViewAt;
}
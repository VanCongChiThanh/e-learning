package com.pbl.elearning.enrollment.payload.response;

import lombok.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private UUID id;                   
    private UUID enrollmentId;        
    private UUID lectureId;            
    private Boolean isCompleted;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime lastViewedAt;  
    private OffsetDateTime completionDate; 
    private OffsetDateTime createdAt;  
    private OffsetDateTime updatedAt; 
}
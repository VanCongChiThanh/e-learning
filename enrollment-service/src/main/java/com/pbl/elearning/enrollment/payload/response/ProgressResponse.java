package com.pbl.elearning.enrollment.payload.response;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private UUID id;                   
    private UUID enrollmentId;        
    private UUID lectureId;            
    private Boolean isCompleted;      
    private Integer watchTimeMinutes;  
    private OffsetDateTime completionDate; 
    private OffsetDateTime createdAt;  
    private OffsetDateTime updatedAt; 
}
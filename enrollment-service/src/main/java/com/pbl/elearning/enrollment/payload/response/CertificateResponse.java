package com.pbl.elearning.enrollment.payload.response;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponse {
    private UUID id;
    private UUID enrollmentId;
    private UUID userId;
    private UUID courseId;
    private String certificateNumber;
    private OffsetDateTime issuedDate;
    private OffsetDateTime expiryDate;
    private String templateUrl;
    private String certificateUrl;
    private Boolean isVerified;
    private OffsetDateTime createdAt;
    
    private String courseName;
    private String courseCode;
    private String userName;
    private String imageUrl;
    private String userEmail;
    private Double completionScore;
    private OffsetDateTime courseCompletionDate;
}
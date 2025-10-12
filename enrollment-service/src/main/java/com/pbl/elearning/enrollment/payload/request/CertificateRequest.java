package com.pbl.elearning.enrollment.payload.request;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequest {
    private UUID enrollmentId;
    private UUID userId;
    private UUID courseId;
    private String certificateNumber;
    private OffsetDateTime issuedDate;
    private OffsetDateTime expiryDate;
    private String templateUrl;
    private String certificateUrl;
    private Boolean isVerified;
}
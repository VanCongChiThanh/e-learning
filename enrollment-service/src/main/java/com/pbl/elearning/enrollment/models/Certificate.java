package com.pbl.elearning.enrollment.models;

import javax.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificates")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollmentId", nullable = false, unique = true)
    private Enrollment enrollment;

    private String certificateNumber;
    private OffsetDateTime issuedDate;
    private OffsetDateTime expiryDate;
    private String templateUrl;
    private String certificateUrl;
    
    @Builder.Default
    private Boolean isVerified = false;
    
    private OffsetDateTime createdAt;
}

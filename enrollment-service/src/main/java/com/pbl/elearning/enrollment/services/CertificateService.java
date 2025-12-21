package com.pbl.elearning.enrollment.services;
import com.pbl.elearning.enrollment.models.EnrollmentCompletedEvent;
import com.pbl.elearning.enrollment.payload.response.CertificateResponse;

import java.util.List;
import java.util.UUID;

public interface CertificateService {
    
    CertificateResponse getOrGenerateCertificate(UUID enrollmentId);
    void generateCertificateAsync(EnrollmentCompletedEvent event);
    CertificateResponse getCertificateById(UUID certificateId);
    CertificateResponse generateCertificate(UUID enrollmentId);

    List<CertificateResponse> getAllCertificatesForUser(UUID userId);
    CertificateResponse verifyCertificateByCode(String code);
}
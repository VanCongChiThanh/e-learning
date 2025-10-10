package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.request.CertificateRequest;
import com.pbl.elearning.enrollment.payload.response.CertificateResponse;

import java.util.List;
import java.util.UUID;

public interface CertificateService {
    
    /**
     * Generate certificate for completed enrollment
     */
    CertificateResponse generateCertificate(UUID enrollmentId);
    
    /**
     * Get certificate by ID
     */
    CertificateResponse getCertificateById(UUID certificateId);
    
    /**
     * Get certificate by enrollment ID
     */
    CertificateResponse getCertificateByEnrollmentId(UUID enrollmentId);
    
    /**
     * Get all certificates for a user
     */
    List<CertificateResponse> getCertificatesByUserId(UUID userId);
    
    /**
     * Get all certificates for a course
     */
    List<CertificateResponse> getCertificatesByCourseId(UUID courseId);
    
    /**
     * Verify certificate by certificate number
     */
    CertificateResponse verifyCertificate(String certificateNumber);
    
    /**
     * Check if enrollment is eligible for certificate
     */
    Boolean isEligibleForCertificate(UUID enrollmentId);
    
    /**
     * Update certificate details
     */
    CertificateResponse updateCertificate(UUID certificateId, CertificateRequest request);
    
    /**
     * Revoke certificate
     */
    void revokeCertificate(UUID certificateId);
}
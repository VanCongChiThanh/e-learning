package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Certificate;
import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.payload.request.CertificateRequest;
import com.pbl.elearning.enrollment.payload.response.CertificateResponse;
import com.pbl.elearning.enrollment.repository.CertificateRepository;
import com.pbl.elearning.enrollment.repository.EnrollmentRepository;
import com.pbl.elearning.enrollment.services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;

    private CertificateResponse mapToResponse(Certificate certificate) {
        Enrollment enrollment = certificate.getEnrollment();
        return CertificateResponse.builder()
                .id(certificate.getId())
                .enrollmentId(enrollment != null ? enrollment.getId() : null)
                .userId(enrollment != null && enrollment.getUser() != null ? enrollment.getUser().getId() : null)
                .courseId(enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null)
                .certificateNumber(certificate.getCertificateNumber())
                .issuedDate(certificate.getIssuedDate())
                .expiryDate(certificate.getExpiryDate())
                .templateUrl(certificate.getTemplateUrl())
                .certificateUrl(certificate.getCertificateUrl())
                .isVerified(certificate.getIsVerified())
                .createdAt(certificate.getCreatedAt())
                .courseName(enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .courseCode(enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getSlug() : null)
                .userName(enrollment != null && enrollment.getUser() != null ? enrollment.getUser().getEmail() : null)
                .userEmail(enrollment != null && enrollment.getUser() != null ? enrollment.getUser().getEmail() : null)
                .completionScore(enrollment != null ? enrollment.getProgressPercentage() : null)
                .courseCompletionDate(certificate.getIssuedDate())
                .build();
    }

    @Override
    public CertificateResponse generateCertificate(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!isEligibleForCertificate(enrollmentId)) {
            throw new RuntimeException("Enrollment is not eligible for certificate");
        }

        Certificate existing = certificateRepository.findByEnrollment_Id(enrollmentId).orElse(null);
        if (existing != null) {
            return mapToResponse(existing);
        }

        Certificate certificate = Certificate.builder()
                .enrollment(enrollment)
                .certificateNumber(generateCertificateNumber())
                .issuedDate(OffsetDateTime.now())
                .expiryDate(null)
                .isVerified(true)
                .createdAt(OffsetDateTime.now())
                .build();

        Certificate saved = certificateRepository.save(certificate);
        return mapToResponse(saved);
    }

    @Override
    public CertificateResponse getCertificateById(UUID certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        return mapToResponse(certificate);
    }

    @Override
    public CertificateResponse getCertificateByEnrollmentId(UUID enrollmentId) {
        Certificate certificate = certificateRepository.findByEnrollment_Id(enrollmentId)
                .orElse(null);
        return certificate != null ? mapToResponse(certificate) : null;
    }

    @Override
    public List<CertificateResponse> getCertificatesByUserId(UUID userId) {
        List<Certificate> certificates = certificateRepository.findByUserId(userId);
        return certificates.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateResponse> getCertificatesByCourseId(UUID courseId) {
        List<Certificate> certificates = certificateRepository.findByCourseId(courseId);
        return certificates.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CertificateResponse verifyCertificate(String certificateNumber) {
        Certificate certificate = certificateRepository.findByCertificateNumber(certificateNumber)
                .orElse(null);
        return certificate != null ? mapToResponse(certificate) : null;
    }

    @Override
    public Boolean isEligibleForCertificate(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        return enrollment.getProgressPercentage() != null && enrollment.getProgressPercentage() >= 100.0;
    }

    @Override
    public CertificateResponse updateCertificate(UUID certificateId, CertificateRequest request) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        certificate.setTemplateUrl(request.getTemplateUrl());
        certificate.setCertificateUrl(request.getCertificateUrl());
        certificate.setExpiryDate(request.getExpiryDate());
        certificate.setIsVerified(request.getIsVerified());

        Certificate updated = certificateRepository.save(certificate);
        return mapToResponse(updated);
    }

    @Override
    public void revokeCertificate(UUID certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        
        certificate.setIsVerified(false);
        certificateRepository.save(certificate);
    }

    private String generateCertificateNumber() {
        return "CERT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}


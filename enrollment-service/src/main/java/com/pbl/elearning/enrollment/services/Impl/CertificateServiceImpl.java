package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Certificate;
import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.models.EnrollmentCompletedEvent;
import com.pbl.elearning.enrollment.payload.response.CertificateResponse;
import com.pbl.elearning.enrollment.repository.CertificateRepository;
import com.pbl.elearning.enrollment.repository.EnrollmentRepository;
import com.pbl.elearning.enrollment.services.CertificateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private CertificateResponse mapToResponse(Certificate certificate) {
        Enrollment enrollment = certificate.getEnrollment();
        return CertificateResponse.builder()
                .id(certificate.getId())
                .enrollmentId(enrollment != null ? enrollment.getId() : null)
                .userId(enrollment != null && enrollment.getUser() != null ? enrollment.getUser().getId() : null)
                .courseId(enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getCourseId()
                        : null)
                .certificateNumber(certificate.getCertificateNumber())
                .issuedDate(certificate.getIssuedDate())
                .expiryDate(certificate.getExpiryDate())
                .templateUrl(certificate.getTemplateUrl())
                .certificateUrl(certificate.getCertificateUrl())
                .isVerified(certificate.getIsVerified())
                .createdAt(certificate.getCreatedAt())
                .courseName(
                        enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .courseCode(
                        enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getSlug() : null)
                .userName(enrollment != null && enrollment.getUser() != null ? enrollment.getUser().getEmail() : null)
                .userEmail(enrollment != null && enrollment.getUser() != null ? enrollment.getUser().getEmail() : null)
                .completionScore(enrollment != null ? enrollment.getProgressPercentage() : null)
                .courseCompletionDate(certificate.getIssuedDate())
                .imageUrl(enrollment != null && enrollment.getCourse() != null ? enrollment.getCourse().getImage() : null)
                .build();
    }
    
        
    public Certificate mapToEntity(CertificateResponse resp) {
        if (resp == null) return null;

        Certificate entity = new Certificate();
        entity.setId(resp.getId());
        if (resp.getEnrollmentId() != null) {
            Enrollment enrollment = enrollmentRepository.findById(resp.getEnrollmentId())
                    .orElseThrow(() -> new RuntimeException("Enrollment not found: " + resp.getEnrollmentId()));
            entity.setEnrollment(enrollment);
        }

        entity.setCertificateNumber(resp.getCertificateNumber());
        entity.setIssuedDate(resp.getIssuedDate());
        entity.setExpiryDate(resp.getExpiryDate());
        entity.setTemplateUrl(resp.getTemplateUrl());
        entity.setCertificateUrl(resp.getCertificateUrl());
        entity.setIsVerified(resp.getIsVerified() != null ? resp.getIsVerified() : false);
        entity.setCreatedAt(resp.getCreatedAt());

        return entity;
    }


    // Sync API trả về Certificate (đã tạo)
    public CertificateResponse getOrGenerateCertificate(UUID enrollmentId) {
        CertificateResponse cert = mapToResponse(certificateRepository.findByEnrollment_Id(enrollmentId));
        if (cert == null) {
            cert = generateCertificate(enrollmentId);
        }
        return cert;
    }

    // Async generation
    @Async
    @Override
    @EventListener
    public void generateCertificateAsync(EnrollmentCompletedEvent event) {
        UUID enrollmentId = event.getEnrollmentId();

        // 1. Check if certificate exists
        if (certificateRepository.existsByEnrollmentId(enrollmentId)) return;

        try {
            // 2. Generate certificate record
            CertificateResponse cert = generateCertificate(enrollmentId);

            // 3. Render template → PDF
            String template = pdfService.loadTemplate("index.html");
            Map<String, Object> vars = pdfService.toTemplateVariables(cert);
            String html = pdfService.renderTemplateWithMap(template, vars);
            byte[] pdfBytes = pdfService.generatePdfFromHtml(html);

            // 4. Upload PDF → get S3 URL
            String s3Url = pdfService.uploadPdfToS3(pdfBytes, cert.getCertificateNumber() + ".pdf");

            // 5. Update certificate record
            cert.setCertificateUrl(s3Url);
            certificateRepository.save(mapToEntity(cert));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CertificateResponse generateCertificate(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));


        Certificate existing = certificateRepository.findByEnrollment_Id(enrollmentId);
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
    private String generateCertificateNumber() {
        return "CERT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    @Override
    public List<CertificateResponse> getAllCertificatesForUser(UUID userId) {
        List<Certificate> certificates = certificateRepository.findAllByEnrollment_User_Id(userId);
        return certificates.stream().map(this::mapToResponse).toList();
    }

    @Override
    public CertificateResponse getCertificateById(UUID certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found: " + certificateId));
        return mapToResponse(certificate);
    }
}


package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.payload.request.CertificateRequest;
import com.pbl.elearning.enrollment.payload.response.CertificateResponse;
import com.pbl.elearning.enrollment.services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/generate/{enrollmentId}")
    public ResponseEntity<CertificateResponse> generateCertificate(@PathVariable UUID enrollmentId) {
        CertificateResponse response = certificateService.generateCertificate(enrollmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateResponse> getCertificateById(@PathVariable UUID id) {
        CertificateResponse response = certificateService.getCertificateById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<CertificateResponse> getCertificateByEnrollmentId(@PathVariable UUID enrollmentId) {
        CertificateResponse response = certificateService.getCertificateByEnrollmentId(enrollmentId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateResponse>> getCertificatesByUserId(@PathVariable UUID userId) {
        List<CertificateResponse> responses = certificateService.getCertificatesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CertificateResponse>> getCertificatesByCourseId(@PathVariable UUID courseId) {
        List<CertificateResponse> responses = certificateService.getCertificatesByCourseId(courseId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/verify/{certificateNumber}")
    public ResponseEntity<CertificateResponse> verifyCertificate(@PathVariable String certificateNumber) {
        CertificateResponse response = certificateService.verifyCertificate(certificateNumber);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/enrollment/{enrollmentId}/eligible")
    public ResponseEntity<Boolean> isEligibleForCertificate(@PathVariable UUID enrollmentId) {
        Boolean eligible = certificateService.isEligibleForCertificate(enrollmentId);
        return ResponseEntity.ok(eligible);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificateResponse> updateCertificate(
            @PathVariable UUID id, 
            @RequestBody CertificateRequest request) {
        CertificateResponse response = certificateService.updateCertificate(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/revoke")
    public ResponseEntity<Void> revokeCertificate(@PathVariable UUID id) {
        certificateService.revokeCertificate(id);
        return ResponseEntity.noContent().build();
    }
}
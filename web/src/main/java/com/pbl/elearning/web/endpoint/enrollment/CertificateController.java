package com.pbl.elearning.web.endpoint.enrollment;
import com.pbl.elearning.enrollment.payload.response.CertificateResponse;
import com.pbl.elearning.enrollment.services.CertificateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @GetMapping("/get-or-generate")
    public ResponseEntity<String> getCertificate(@RequestParam UUID enrollmentId) {
        try {
            CertificateResponse cert = certificateService.getOrGenerateCertificate(enrollmentId);

            if (cert.getCertificateUrl() != null && !cert.getCertificateUrl().isEmpty()) {
                return ResponseEntity.ok(cert.getCertificateUrl());
            } else {
                return ResponseEntity.ok("Certificate record created, PDF generation in progress");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating certificate");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllCertificatesForUser(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(certificateService.getAllCertificatesForUser(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving certificates for user");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificateById(@PathVariable UUID id) {
        try {
            CertificateResponse cert = certificateService.getCertificateById(id);
            if (cert != null) {
                return ResponseEntity.ok(cert);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Certificate not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving certificate");
        }
    }

    @GetMapping("/verify-certificate")
    public ResponseEntity<?> verifyCertificate(@RequestParam String code) {
        try {
            CertificateResponse cert = certificateService.verifyCertificateByCode(code);
            if (cert != null) {
                return ResponseEntity.ok(cert);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Certificate not found for the provided code");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying certificate");
        }
    }
}


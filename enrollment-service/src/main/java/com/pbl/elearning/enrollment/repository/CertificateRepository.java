package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
}

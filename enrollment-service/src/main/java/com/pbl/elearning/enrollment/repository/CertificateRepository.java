package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Certificate;
import com.pbl.elearning.enrollment.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    Optional<Certificate> findByEnrollment(Enrollment enrollment);
    Certificate findByEnrollment_Id(UUID enrollmentId);
    boolean existsByEnrollmentId(UUID enrollmentId);
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    
    @Query("SELECT c FROM Certificate c WHERE c.enrollment.course.courseId = :courseId")
    List<Certificate> findByCourseId(@Param("courseId") UUID courseId);

    List<Certificate> findAllByEnrollment_User_Id(UUID userId);
}

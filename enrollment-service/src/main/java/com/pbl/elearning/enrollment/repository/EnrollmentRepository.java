package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    List<Enrollment> findByUserId(UUID userId);
    List<Enrollment> findByCourseId(UUID courseId);
}

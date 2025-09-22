package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgressRepository extends JpaRepository<Progress, UUID> {
    List<Progress> findByEnrollmentId(UUID enrollmentId);
}

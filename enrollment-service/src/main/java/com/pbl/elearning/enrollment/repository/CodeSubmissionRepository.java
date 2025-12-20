package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, UUID>, JpaSpecificationExecutor<CodeSubmission> {
    boolean existsByExerciseIdAndUserId(UUID exerciseId, UUID userId);
}
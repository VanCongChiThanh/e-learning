package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, UUID> {
    List<AssignmentSubmission> findByAssignmentId(UUID assignmentId);
    List<AssignmentSubmission> findByUserId(UUID userId);
}
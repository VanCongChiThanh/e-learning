package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.AssignmentSubmission;
import com.pbl.elearning.enrollment.models.Assignment;
import com.pbl.elearning.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, UUID> {
    List<AssignmentSubmission> findByAssignment(Assignment assignment);
    List<AssignmentSubmission> findByAssignment_Id(UUID assignmentId);
    List<AssignmentSubmission> findByUser(User user);
    List<AssignmentSubmission> findByUser_Id(UUID userId);
}
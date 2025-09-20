package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.request.AssignmentSubmissionRequest;
import com.pbl.elearning.enrollment.payload.response.AssignmentSubmissionResponse;

import java.util.List;
import java.util.UUID;

public interface AssignmentSubmissionService {
    AssignmentSubmissionResponse createSubmission(AssignmentSubmissionRequest request);
    AssignmentSubmissionResponse getSubmissionById(UUID id);
    List<AssignmentSubmissionResponse> getSubmissionsByAssignment(UUID assignmentId);
    List<AssignmentSubmissionResponse> getSubmissionsByUser(UUID userId);
    AssignmentSubmissionResponse gradeSubmission(UUID id, Integer score, String feedback, UUID gradedBy);
}
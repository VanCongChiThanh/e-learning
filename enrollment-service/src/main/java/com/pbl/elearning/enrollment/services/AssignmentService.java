package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.request.AssignmentRequest;
import com.pbl.elearning.enrollment.payload.response.AssignmentResponse;

import java.util.List;
import java.util.UUID;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest request);
    AssignmentResponse getAssignmentById(UUID id);
    List<AssignmentResponse> getAssignmentsByCourseId(UUID courseId);
    AssignmentResponse updateAssignment(UUID id, AssignmentRequest request);
    void deleteAssignment(UUID id);
}
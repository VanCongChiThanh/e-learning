package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.*;
import com.pbl.elearning.enrollment.payload.request.AssignmentSubmissionRequest;
import com.pbl.elearning.enrollment.payload.response.AssignmentSubmissionResponse;
import com.pbl.elearning.enrollment.repository.*;
import com.pbl.elearning.enrollment.services.AssignmentSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

    private final AssignmentSubmissionRepository repository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    private AssignmentSubmissionResponse mapToResponse(AssignmentSubmission submission) {
        return AssignmentSubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .userId(submission.getUserId())
                .enrollmentId(submission.getEnrollment().getId())
                .submissionText(submission.getSubmissionText())
                .fileId(submission.getFileId())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .gradedAt(submission.getGradedAt())
                .gradedBy(submission.getGradedBy())
                .build();
    }

    @Override
    public AssignmentSubmissionResponse createSubmission(AssignmentSubmissionRequest request) {
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignment(assignment)
                .userId(request.getUserId())
                .enrollment(enrollment)
                .submissionText(request.getSubmissionText())
                .fileId(request.getFileId())
                .status(request.getStatus() != null ? request.getStatus() : com.pbl.elearning.enrollment.Enum.SubmissionStatus.SUBMITTED)
                .submittedAt(OffsetDateTime.now())
                .build();

        AssignmentSubmission saved = repository.save(submission);
        return mapToResponse(saved);
    }

    @Override
    public AssignmentSubmissionResponse getSubmissionById(UUID id) {
        AssignmentSubmission submission = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        return mapToResponse(submission);
    }

    @Override
    public List<AssignmentSubmissionResponse> getSubmissionsByAssignment(UUID assignmentId) {
        return repository.findByAssignmentId(assignmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentSubmissionResponse> getSubmissionsByUser(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentSubmissionResponse gradeSubmission(UUID id, Integer score, String feedback, UUID gradedBy) {
        AssignmentSubmission submission = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setGradedBy(gradedBy);
        submission.setGradedAt(OffsetDateTime.now());
        submission.setStatus(com.pbl.elearning.enrollment.Enum.SubmissionStatus.GRADED);

        AssignmentSubmission updated = repository.save(submission);
        return mapToResponse(updated);
    }
}
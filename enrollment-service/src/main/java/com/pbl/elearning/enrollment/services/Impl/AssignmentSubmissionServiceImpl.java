package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.*;
import com.pbl.elearning.enrollment.payload.request.AssignmentSubmissionRequest;
import com.pbl.elearning.enrollment.payload.response.AssignmentSubmissionResponse;
import com.pbl.elearning.enrollment.repository.*;
import com.pbl.elearning.enrollment.services.AssignmentSubmissionService;
import com.pbl.elearning.security.domain.User;
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
                .userId(submission.getUser() != null ? submission.getUser().getId() : null)
                .enrollmentId(submission.getEnrollment().getId())
                .submissionText(submission.getSubmissionText())
                .fileId(submission.getFileId())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .gradedAt(submission.getGradedAt())
                .gradedBy(submission.getGradedBy() != null ? submission.getGradedBy().getId() : null)
                .build();
    }

    @Override
    public AssignmentSubmissionResponse createSubmission(AssignmentSubmissionRequest request) {
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        User user = new User();
        user.setId(request.getUserId());

        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignment(assignment)
                .user(user)
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
        return repository.findByAssignment_Id(assignmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentSubmissionResponse> getSubmissionsByUser(UUID userId) {
        return repository.findByUser_Id(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentSubmissionResponse gradeSubmission(UUID id, Integer score, String feedback, UUID gradedBy) {
        AssignmentSubmission submission = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        User gradedByUser = new User();
        gradedByUser.setId(gradedBy);

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setGradedBy(gradedByUser);
        submission.setGradedAt(OffsetDateTime.now());
        submission.setStatus(com.pbl.elearning.enrollment.Enum.SubmissionStatus.GRADED);

        AssignmentSubmission updated = repository.save(submission);
        return mapToResponse(updated);
    }
}
package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.models.CodeSubmission;
import com.pbl.elearning.enrollment.payload.response.CodeSubmissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface CodeSubmissionService {
    Page<CodeSubmissionResponse> getSubmissionsByExerciseAndUser(
            UUID exerciseId,
            UUID userId,
            Pageable pageable,
            Specification<CodeSubmission> spec
    );
    boolean hasUserSubmitted(UUID exerciseId, UUID userId);
}
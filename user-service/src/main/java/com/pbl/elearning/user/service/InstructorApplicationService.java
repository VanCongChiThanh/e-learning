package com.pbl.elearning.user.service;

import com.pbl.elearning.user.domain.enums.ApplicationStatus;
import com.pbl.elearning.user.payload.request.instructor.ApplyInstructorRequest;
import com.pbl.elearning.user.payload.request.instructor.ReviewApplicationRequest;
import com.pbl.elearning.user.payload.response.instructor.ApplyInstructorResponse;
import com.pbl.elearning.user.payload.response.instructor.InstructorCandidateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InstructorApplicationService {
    Page<InstructorCandidateResponse> getAllApplications(Pageable pageable,ApplicationStatus status,ApplicationStatus excludeStatus) ;
    ApplyInstructorResponse applyForInstructor(ApplyInstructorRequest request, UUID userId);
    void cancelApplication(UUID applicationId,UUID userId);
    ApplicationStatus reviewApplication(UUID applicationId, ReviewApplicationRequest request);
}
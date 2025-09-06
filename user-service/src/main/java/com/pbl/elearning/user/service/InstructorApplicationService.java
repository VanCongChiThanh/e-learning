package com.pbl.elearning.user.service;

import com.pbl.elearning.user.domain.enums.ApplicationStatus;
import com.pbl.elearning.user.payload.request.instructor.ApplyInstructorRequest;
import com.pbl.elearning.user.payload.request.instructor.ReviewApplicationRequest;
import com.pbl.elearning.user.payload.response.instructor.ApplyInstructorResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface InstructorApplicationService {
    ApplyInstructorResponse applyForInstructor(ApplyInstructorRequest request, UUID userId);
    void cancelApplication(UUID applicationId,UUID userId);
    ApplicationStatus reviewApplication(UUID applicationId, ReviewApplicationRequest request);
}
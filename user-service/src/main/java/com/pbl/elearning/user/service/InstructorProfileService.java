package com.pbl.elearning.user.service;

import com.pbl.elearning.user.domain.InstructorProfile;
import com.pbl.elearning.user.payload.request.instructor.InstructorProfileRequest;
import com.pbl.elearning.user.payload.response.instructor.InstructorProfileResponse;

import java.util.UUID;

public interface InstructorProfileService {
    InstructorProfile createProfile(UUID userId);
    InstructorProfileResponse updateProfile(InstructorProfileRequest instructorProfile, UUID userId);
    InstructorProfileResponse getProfile(UUID userId);
}
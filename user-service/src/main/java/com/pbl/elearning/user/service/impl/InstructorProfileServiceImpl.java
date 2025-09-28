package com.pbl.elearning.user.service.impl;

import com.pbl.elearning.user.domain.InstructorProfile;
import com.pbl.elearning.user.payload.request.instructor.InstructorProfileRequest;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import com.pbl.elearning.user.payload.response.instructor.InstructorProfileResponse;
import com.pbl.elearning.user.repository.InstructorProfileRepository;
import com.pbl.elearning.user.service.InstructorProfileService;
import com.pbl.elearning.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InstructorProfileServiceImpl implements InstructorProfileService {
    private final InstructorProfileRepository instructorProfileRepository;
    private final UserInfoService userInfoService;

    @Override
    public InstructorProfileResponse updateProfile(InstructorProfileRequest instructorProfile, UUID userId) {
        InstructorProfile profile = this.findByUserId(userId);
        if(instructorProfile.getHeadline() != null){
            profile.setHeadline(instructorProfile.getHeadline());
        }
        if (instructorProfile.getBiography() != null) {
            profile.setBiography(instructorProfile.getBiography());
        }
        if(instructorProfile.getFacebook() != null) {
            profile.setFacebook(instructorProfile.getFacebook());
        }
        if(instructorProfile.getLinkedin() != null) {
            profile.setLinkedin(instructorProfile.getLinkedin());
        }
        if(instructorProfile.getYoutube() != null) {
            profile.setFacebook(instructorProfile.getFacebook());
        }
        if(instructorProfile.getYoutube() != null) {
            profile.setYoutube(instructorProfile.getYoutube());
        }
        if(instructorProfile.getLinkedin() != null) {
            profile.setLinkedin(instructorProfile.getLinkedin());
        }
        return InstructorProfileResponse.toResponse(instructorProfileRepository.save(profile),null);
    }

    @Override
    public InstructorProfileResponse getProfile(UUID userId) {
        InstructorProfile profile = this.findByUserId(userId);
        UserInfoResponse userInfo = UserInfoResponse.toResponse(this.userInfoService.getUserInfoByUserId(userId));
        return InstructorProfileResponse.toResponse(profile, userInfo);
    }

    @Override
    public InstructorProfile createProfile(UUID userId) {
        InstructorProfile instructorProfile= this.findByUserId(userId);
        Optional<InstructorProfile> existingProfile = instructorProfileRepository.findByUserId(userId);
        if (existingProfile.isPresent()) {
            return existingProfile.get();
        }
        InstructorProfile profile = new InstructorProfile();
        profile.setUserId(userId);
        return instructorProfileRepository.save(profile);
    }

    private InstructorProfile findByUserId(UUID userId) {
        return instructorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor profile not found for user: " + userId));
    }
}
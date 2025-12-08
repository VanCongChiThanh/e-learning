package com.pbl.elearning.user.service.impl;


import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.domain.enums.Role;
import com.pbl.elearning.common.exception.BadRequestException;
import com.pbl.elearning.common.exception.ConflictException;
import com.pbl.elearning.common.exception.ForbiddenException;
import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.email.service.EmailService;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.user.domain.InstructorApplication;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.domain.enums.ApplicationStatus;
import com.pbl.elearning.user.payload.request.instructor.ApplyInstructorRequest;
import com.pbl.elearning.user.payload.request.instructor.ReviewApplicationRequest;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import com.pbl.elearning.user.payload.response.instructor.ApplyInstructorResponse;
import com.pbl.elearning.user.payload.response.instructor.InstructorCandidateResponse;
import com.pbl.elearning.user.repository.InstructorApplicationRepository;
import com.pbl.elearning.user.service.InstructorApplicationService;
import com.pbl.elearning.user.service.UserInfoService;
import com.pbl.elearning.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InstructorApplicationServiceImpl implements InstructorApplicationService {
    private static final Duration REAPPLY_COOLDOWN = Duration.ofDays(14);
    private final InstructorApplicationRepository instructorApplicationRepository;
    private final InstructorProfileServiceImpl instructorProfileService;
    private final EmailService emailService;
    private final UserInfoService userInfoService;
    private final UserService userService;

    @Override
    public Page<InstructorCandidateResponse> getAllApplications(Pageable pageable,ApplicationStatus status,ApplicationStatus excludeStatus) {
        Page<Object[]> results = instructorApplicationRepository.findAllWithUserInfo(pageable,status,excludeStatus);

        return results.map(record -> {
            InstructorApplication app = (InstructorApplication) record[0];
            UserInfo ui = (UserInfo) record[1];

            UserInfoResponse userInfo = UserInfoResponse.toResponse(ui);

            return InstructorCandidateResponse.toResponse(
                    userInfo,
                    app
            );
        });
    }


    @Override
    public ApplyInstructorResponse applyForInstructor(ApplyInstructorRequest request, UUID userId) {
        User user = userService.findById(userId);

        if (user.getRole() == Role.ROLE_INSTRUCTOR) {
            throw new ForbiddenException(MessageConstant.ALREADY_INSTRUCTOR);
        }
        if (!Boolean.TRUE.equals(user.getIsEnabled()) || user.getConfirmedAt() == null) {
            throw new ForbiddenException(MessageConstant.USER_NOT_VERIFIED);
        }

        instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .ifPresent(last -> {
                    if (last.getStatus() == ApplicationStatus.PENDING) {
                        throw new ConflictException(MessageConstant.APPLICATION_ALREADY_PENDING);
                    }
                    if (last.getStatus() == ApplicationStatus.APPROVED) {
                        throw new ConflictException(MessageConstant.APPLICATION_ALREADY_APPROVED);
                    }
                    if (last.getStatus() == ApplicationStatus.REJECTED &&
                            last.getUpdatedAt() != null &&
                            last.getUpdatedAt().toInstant().plus(REAPPLY_COOLDOWN).isAfter(Instant.now())) {
                        throw new ConflictException(MessageConstant.APPLICATION_COOLDOWN);
                    }
                });

        if (request.getMotivation() == null || request.getMotivation().trim().length() < 50) {
            throw new BadRequestException(MessageConstant.MOTIVATION_TOO_SHORT);
        }

        // Save the CV file using FileHandlerService
        InstructorApplication application = new InstructorApplication();
        application.setUserId(userId);
        application.setPortfolioLink(request.getPortfolioLink());
        application.setMotivation(request.getMotivation());
        application.setCvUrl(request.getCvUrl());
        application.setStatus(ApplicationStatus.PENDING);
        application.setExtraInfo(request.getExtraInfo());
        instructorApplicationRepository.save(application);
        return ApplyInstructorResponse.toResponse(application);
    }

    @Override
    public void cancelApplication(UUID applicationId, UUID userId) {
        InstructorApplication application = instructorApplicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new NotFoundException(MessageConstant.APPLICATION_NOT_FOUND));
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel application that is not pending");
        }
        application.setStatus(ApplicationStatus.CANCELED);
        instructorApplicationRepository.save(application);
    }

    @Override
    public ApplicationStatus reviewApplication(UUID applicationId, ReviewApplicationRequest request) {
        ApplicationStatus status = request.getStatus();
        InstructorApplication application = instructorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException(MessageConstant.APPLICATION_NOT_FOUND));
        UserInfo userInfo = userInfoService.getUserInfoByUserId(application.getUserId());
        switch (status) {
            case APPROVED:
                emailService.sendMailConfirmInstructorApplication(
                        userInfo.getFirstName(),
                        userInfo.getLastName(),
                        userInfo.getEmail(),
                        application.getId(),
                        "vi"
                );
                application.setStatus(status);
                instructorProfileService.createProfile(application.getUserId());
                userService.updateRoleUser(application.getUserId(), Role.ROLE_INSTRUCTOR);
                break;
            case REJECTED:
                emailService.sendMailRejectInstructorApplication(
                        userInfo.getFirstName(),
                        userInfo.getLastName(),
                        userInfo.getEmail(),
                        application.getId(),
                        request.getReason(),
                        "vi"
                );
                application.setStatus(status);
                break;
            default:
                throw new BadRequestException(MessageConstant.INVALID_STATUS_REVIEW_APPLICATION);
        }
        instructorApplicationRepository.save(application);
        return status;
    }
}
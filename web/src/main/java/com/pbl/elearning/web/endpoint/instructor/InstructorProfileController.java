package com.pbl.elearning.web.endpoint.instructor;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.user.payload.request.instructor.InstructorProfileRequest;
import com.pbl.elearning.user.service.InstructorProfileService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/instructor")
@RestController
@RequiredArgsConstructor
public class InstructorProfileController {
    private final InstructorProfileService instructorProfileService;
    @GetMapping("/profile/{id}")
    @ApiOperation(value = "Get instructor profile by ID")
    ResponseEntity<ResponseDataAPI> getInstructorProfile(
            @PathVariable("id") UUID userId
    ) {
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                instructorProfileService.getProfile(userId)));
    }
    @PatchMapping("/profile/me/update")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @ApiOperation(value = "Update current instructor profile")
    ResponseEntity<ResponseDataAPI> updateInstructorProfile(
            @RequestBody InstructorProfileRequest instructorProfile,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                instructorProfileService.updateProfile(instructorProfile, userPrincipal.getId())));
    }
    @GetMapping("/profile/me")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    ResponseEntity<ResponseDataAPI> getMyInstructorProfile(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                instructorProfileService.getProfile(userPrincipal.getId())));
    }
}
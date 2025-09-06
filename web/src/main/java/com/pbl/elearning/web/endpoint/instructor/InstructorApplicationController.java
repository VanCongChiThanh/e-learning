package com.pbl.elearning.web.endpoint.instructor;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.user.payload.request.instructor.ApplyInstructorRequest;
import com.pbl.elearning.user.payload.request.instructor.ReviewApplicationRequest;
import com.pbl.elearning.user.service.InstructorApplicationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/instructor/applications")
@RestController
@RequiredArgsConstructor
public class InstructorApplicationController {
    private final InstructorApplicationService instructorApplicationService;
    @PostMapping("/apply")
    @ApiOperation("Apply for instructor role")
    ResponseEntity<ResponseDataAPI> applyForInstructor(
            @RequestBody ApplyInstructorRequest applyInstructorRequest,
            @CurrentUser UserPrincipal userPrincipal
            ) {
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                instructorApplicationService.applyForInstructor(applyInstructorRequest, userPrincipal.getId())));
    }
    @PatchMapping("/{applicationId}/cancel")
    @ApiOperation("Cancel instructor application")
    ResponseEntity<ResponseDataAPI> cancelApplication(
            @PathVariable("applicationId") UUID applicationId,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        instructorApplicationService.cancelApplication(applicationId, userPrincipal.getId());
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(null));
    }
    @PatchMapping("/{applicationId}/review")
    @ApiOperation("Review instructor application")
    ResponseEntity<ResponseDataAPI> reviewApplication(
            @PathVariable("applicationId") UUID applicationId,
            @RequestBody ReviewApplicationRequest reviewRequest
    ) {
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                instructorApplicationService.reviewApplication(applicationId, reviewRequest)));
    }

}
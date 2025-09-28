package com.pbl.elearning.web.endpoint.instructor;

import com.pbl.elearning.common.PagingUtils;
import com.pbl.elearning.common.payload.general.PageInfo;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.user.payload.request.instructor.ApplyInstructorRequest;
import com.pbl.elearning.user.payload.request.instructor.ReviewApplicationRequest;
import com.pbl.elearning.user.payload.response.instructor.InstructorCandidateResponse;
import com.pbl.elearning.user.service.InstructorApplicationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/instructor/applications")
@RestController
@RequiredArgsConstructor
public class InstructorApplicationController {
    private final InstructorApplicationService instructorApplicationService;
    @GetMapping("/all")
    @ApiOperation("Get all instructor applications (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ResponseDataAPI> getAllApplications(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "paging", defaultValue = "5") int paging,
            @RequestParam(value = "sort", defaultValue = "created_at") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order) {
        Pageable pageable = PagingUtils.makePageRequest(sort, order, page, paging);
        Page<InstructorCandidateResponse> applications = instructorApplicationService.getAllApplications(pageable);
        PageInfo pageInfo = new PageInfo(
                pageable.getPageNumber() + 1,
                applications.getTotalPages(),
                applications.getTotalElements()
        );
        return ResponseEntity.ok(ResponseDataAPI.success(applications.getContent(), pageInfo));
    }
    @PostMapping("/apply")
    @ApiOperation("Apply for instructor role")
    @PreAuthorize("hasRole('LEARNER')")
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
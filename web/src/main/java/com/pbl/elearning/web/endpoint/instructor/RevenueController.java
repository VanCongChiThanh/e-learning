package com.pbl.elearning.web.endpoint.instructor;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.enrollment.payload.request.InstructorRevenueRequest;
import com.pbl.elearning.enrollment.services.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1")
@RestController
@RequiredArgsConstructor
public class RevenueController {
    private final RevenueService revenueService;
    @PostMapping("/instructors/me/revenue")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ResponseDataAPI> getMyRevenueReport(
            @RequestBody InstructorRevenueRequest request,
            @CurrentUser UserPrincipal userPrincipal
            ) {
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                revenueService.getInstructorRevenueReport(userPrincipal.getId(), request)));
    }
    @PostMapping("/instructors/revenues")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDataAPI> getAllInstructorsRevenue(
            @RequestBody InstructorRevenueRequest request
    ) {
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                revenueService.getAllInstructorRevenue(request)));
    }
}
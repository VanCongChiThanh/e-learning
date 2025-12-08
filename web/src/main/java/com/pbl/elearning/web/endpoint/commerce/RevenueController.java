package com.pbl.elearning.web.endpoint.commerce;

import com.pbl.elearning.commerce.service.RevenueService;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.commerce.payload.request.InstructorRevenueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

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
    @GetMapping("/courses/{courseId}/transactions")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDataAPI> getCourseTransactions(
            @PathVariable("courseId") UUID courseId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        Instant startInstant = (startDate != null)
                ? Instant.parse(startDate)
                : Instant.EPOCH;  // mặc định từ 1970 → lấy hết

        Instant endInstant = (endDate != null)
                ? Instant.parse(endDate)
                : Instant.now(); // mặc định tới hiện tại
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                revenueService.getCourseTransactions(
                        courseId,userPrincipal.getId(),
                        startInstant,
                        endInstant
                )));
    }
    @PostMapping("/instructors/me/courses/revenue")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ResponseDataAPI> getMyCoursesRevenue(
            @RequestBody InstructorRevenueRequest request,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                revenueService.getCourseRevenueByInstructor(
                        userPrincipal.getId(),
                        request
                )));
    }
}
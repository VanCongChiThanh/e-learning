package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.commerce.PagingUtils;
import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.PageInfo;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.enrollment.models.CodeSubmission;
import com.pbl.elearning.enrollment.payload.response.CodeExerciseResponse;
import com.pbl.elearning.enrollment.payload.response.CodeSubmissionResponse;
import com.pbl.elearning.enrollment.services.CodeExerciseService;
import com.pbl.elearning.enrollment.services.CodeSubmissionService;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/code-exercises")
@RequiredArgsConstructor
public class CodeExerciseController {
    private final CodeExerciseService codeExerciseService;
    private final CodeSubmissionService codeSubmissionService;

    @GetMapping("/{exerciseId}")
    public ResponseEntity<ResponseDataAPI> getCodeExerciseById(@PathVariable UUID exerciseId) {
        CodeExerciseResponse response = codeExerciseService.getCodeExerciseById(exerciseId);
        return ResponseEntity.ok(ResponseDataAPI.success(response, "Code exercise retrieved successfully"));
    }

    @GetMapping("/by-lecture/{lectureId}")
    public ResponseEntity<ResponseDataAPI> getAllCodeExercisesByLectureId(@PathVariable UUID lectureId) {
        List<CodeExerciseResponse> response = codeExerciseService.getAllCodeExercisesByLectureId(lectureId);
        return ResponseEntity.ok(ResponseDataAPI.success(response, "Code exercises for lecture retrieved successfully"));
    }
    @GetMapping("/{exerciseId}/my-submissions")
    @PreAuthorize("hasAnyRole('LEARNER', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> getMySubmissions(
            @PathVariable UUID exerciseId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "paging", defaultValue = "10") int paging,
            // Mặc định sắp xếp theo thời gian nộp mới nhất
            @RequestParam(value = "sort", defaultValue = "submittedAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @Filter Specification<CodeSubmission> specification,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        UUID userId = userPrincipal.getId();

        Pageable pageable = PagingUtils.makePageRequest(sort, order, page, paging);


        Page<CodeSubmissionResponse> submissionPage = codeSubmissionService
                .getSubmissionsByExerciseAndUser(exerciseId, userId, pageable, specification);

        PageInfo pageInfo = new PageInfo(
                page,
                submissionPage.getTotalPages(),
                submissionPage.getTotalElements()
        );

        return ResponseEntity.ok(ResponseDataAPI.success(submissionPage.getContent(), pageInfo));
    }

    @GetMapping("/{exerciseId}/submission/check-status")
    @PreAuthorize("hasAnyRole('LEARNER', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> checkSubmissionStatus(
            @PathVariable UUID exerciseId,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        UUID userId = userPrincipal.getId();


        boolean hasSubmitted = codeSubmissionService.hasUserSubmitted(exerciseId, userId);

        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(hasSubmitted) // Trả về true hoặc false
                .build());
    }

}

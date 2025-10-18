package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.commerce.PagingUtils;
import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.PageInfo;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.domain.Review;
import com.pbl.elearning.course.payload.request.ReviewRequest;
import com.pbl.elearning.course.payload.response.ReviewResponse;
import com.pbl.elearning.course.service.ReviewService;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/courses/{courseId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping
    @PreAuthorize("hasAnyRole('LEARNER','INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> createReview(
            @PathVariable UUID courseId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);

        ReviewResponse reviewResponse = reviewService.createReview(request, courseId, userId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(reviewResponse)
                .build());
    }
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseDataAPI> getReviewById(@PathVariable UUID reviewId) {
        ReviewResponse reviewResponse = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(reviewResponse)
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseDataAPI> getReviewsByCourse(@PathVariable UUID courseId) {
        Set<ReviewResponse> reviews = reviewService.getReviewsByCourseId(courseId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(reviews)
                .build());
    }
    @GetMapping("/Page")
    public ResponseEntity<ResponseDataAPI> getReviewsPageByCourse(
            @PathVariable UUID courseId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "paging", defaultValue = "5") int paging,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @Filter Specification<Review> specification) {

        Pageable pageable = PagingUtils.makePageRequest(sort, order, page, paging);


        Page<ReviewResponse> reviewPage = reviewService.getReviewsPageByCourseId(courseId, pageable, specification);

        PageInfo pageInfo = new PageInfo(
                page,
                reviewPage.getTotalPages(),
                reviewPage.getTotalElements()
        );


        return ResponseEntity.ok(ResponseDataAPI.success(reviewPage.getContent(), pageInfo));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('LEARNER','INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        ReviewResponse updatedReview = reviewService.updateReview(reviewId, request, userId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(updatedReview)
                .build());
    }
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('LEARNER','INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> deleteReview(@PathVariable UUID reviewId, Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Review deleted successfully")
                .build());
    }

    private UUID getUserIdFromAuthentication(Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() != null) {
            // Example implementation - adjust based on your User details implementation
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        }

        throw new RuntimeException("User not authenticated");
    }


}

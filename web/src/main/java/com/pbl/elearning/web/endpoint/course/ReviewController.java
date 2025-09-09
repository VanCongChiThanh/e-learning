package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.payload.request.ReviewRequest;
import com.pbl.elearning.course.payload.response.ReviewResponse;
import com.pbl.elearning.course.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseDataAPI> createReview(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody ReviewRequest request) {
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
    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseDataAPI> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse updatedReview = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(updatedReview)
                .build());
    }
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseDataAPI> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Review deleted successfully")
                .build());
    }

}

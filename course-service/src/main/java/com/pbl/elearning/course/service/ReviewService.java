package com.pbl.elearning.course.service;

import com.pbl.elearning.course.payload.request.ReviewRequest;
import com.pbl.elearning.course.payload.response.ReviewResponse;

import java.util.Set;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest reviewRequest, UUID courseId, UUID userId);
    ReviewResponse getReviewById(UUID reviewId);
    Set<ReviewResponse> getReviewsByCourseId(UUID courseId);
    ReviewResponse updateReview(UUID reviewId, ReviewRequest reviewRequest);
    void deleteReview(UUID reviewId);

    Double getAverageRatingByCourseId(UUID courseId);
    Integer getTotalReviewsByCourseId(UUID courseId);
}

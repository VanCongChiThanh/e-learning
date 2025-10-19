package com.pbl.elearning.course.service;

import com.pbl.elearning.course.domain.Review;
import com.pbl.elearning.course.domain.enums.VoteType;
import com.pbl.elearning.course.payload.request.ReviewRequest;
import com.pbl.elearning.course.payload.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest reviewRequest, UUID courseId, UUID userId);
    ReviewResponse getReviewById(UUID reviewId);
    Set<ReviewResponse> getReviewsByCourseId(UUID courseId);
    ReviewResponse updateReview(UUID reviewId, ReviewRequest reviewRequest, UUID userId);
    void deleteReview(UUID reviewId, UUID userId);

    Page<ReviewResponse> getReviewsPageByCourseId(UUID courseId, Pageable pageable, Specification<Review> spec);


    Double getAverageRatingByCourseId(UUID courseId);
    Integer getTotalReviewsByCourseId(UUID courseId);

    ReviewResponse replyToReview(UUID parentReviewId, UUID userId, ReviewRequest reviewRequest);
    void voteReview(UUID reviewId, UUID userId, VoteType voteType);
}

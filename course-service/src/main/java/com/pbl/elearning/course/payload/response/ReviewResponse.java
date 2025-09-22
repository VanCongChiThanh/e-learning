package com.pbl.elearning.course.payload.response;

import com.pbl.elearning.course.domain.Review;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ReviewResponse {
    private UUID reviewId;
    private UUID courseId;
    private UUID userId;
    private Integer rating;
    private String comment;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .courseId(review.getCourse().getCourseId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }

}

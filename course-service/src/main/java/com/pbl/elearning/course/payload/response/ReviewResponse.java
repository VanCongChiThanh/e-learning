package com.pbl.elearning.course.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl.elearning.course.domain.Review;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {
    private UUID reviewId;
    private UUID courseId;
    private UUID userId;
    private Integer rating;
    private String comment;
    private String UserName;
    private Timestamp createdAt;
    private String userAvatar;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .courseId(review.getCourse().getCourseId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }
    public static ReviewResponse fromEntityDetail(Review review, String userName, String userAvatar) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .courseId(review.getCourse().getCourseId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .UserName(userName)
                .createdAt(review.getCreatedAt())
                .userAvatar(userAvatar)
                .build();
    }

}

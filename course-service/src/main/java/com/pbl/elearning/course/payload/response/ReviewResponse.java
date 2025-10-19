package com.pbl.elearning.course.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl.elearning.course.domain.Review;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private int likeCount;
    private int dislikeCount;
    private UUID parentReviewId;
    private Set<ReviewResponse> replies;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .courseId(review.getCourse().getCourseId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }

    public static  ReviewResponse fromEntityWithIdRely(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .parentReviewId(review.getParentReview() != null ? review.getParentReview().getReviewId() : null)
                .build();
    }

    public static ReviewResponse fromEntityDetail(Review review, String userName, String userAvatar) {

        Set<ReviewResponse> replyResponses = null;
        if (review.getReplies() != null && !review.getReplies().isEmpty()) {
            // Đệ quy: map các replies của review này
            // Lưu ý: Tạm thời không lấy thông tin chi tiết (userName, avatar) cho các reply cấp 2 để tránh query phức tạp
            replyResponses = review.getReplies().stream()
                    .map(ReviewResponse::fromEntityWithIdRely) // Dùng fromEntity đơn giản cho cấp con
                    .collect(Collectors.toSet());
        }
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .courseId(review.getCourse().getCourseId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .UserName(userName)
                .createdAt(review.getCreatedAt())
                .userAvatar(userAvatar)

                .likeCount(review.getLikeCount())
                .dislikeCount(review.getDislikeCount())
                .parentReviewId(review.getParentReview() != null ? review.getParentReview().getReviewId() : null)
                .replies(replyResponses)
                .build();
    }

}

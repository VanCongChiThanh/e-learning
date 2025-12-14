package com.pbl.elearning.course.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl.elearning.course.domain.Comment;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {

    private UUID commentId;
    private UUID lectureId;
    private UUID userId;
    private String content;

    private String userName;
    private String userAvatar;
    private Timestamp createdAt;

    private int likeCount;
    private int dislikeCount;

    private UUID parentCommentId;
    private Set<CommentResponse> replies;

    public static CommentResponse fromEntity(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .lectureId(comment.getLecture().getLectureId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .build();
    }

    public static CommentResponse fromEntityWithParent(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .parentCommentId(
                        comment.getParentComment() != null
                                ? comment.getParentComment().getCommentId()
                                : null
                )
                .build();
    }

    public static CommentResponse fromEntityDetail(
            Comment comment,
            String userName,
            String userAvatar
    ) {
        Set<CommentResponse> replyResponses = null;

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            replyResponses = comment.getReplies().stream()
                    .map(CommentResponse::fromEntityWithParent)
                    .collect(Collectors.toSet());
        }

        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .lectureId(comment.getLecture().getLectureId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .userName(userName)
                .userAvatar(userAvatar)
                .createdAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .parentCommentId(
                        comment.getParentComment() != null
                                ? comment.getParentComment().getCommentId()
                                : null
                )
                .replies(replyResponses)
                .build();
    }
}

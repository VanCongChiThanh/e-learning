package com.pbl.elearning.course.service;

import com.pbl.elearning.course.domain.Comment;
import com.pbl.elearning.course.domain.enums.VoteType;
import com.pbl.elearning.course.payload.request.CommentRequest;
import com.pbl.elearning.course.payload.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

public interface CommentService {

    CommentResponse createComment(UUID lectureId, UUID userId, CommentRequest request);

    CommentResponse getCommentById(UUID commentId);

    Set<CommentResponse> getCommentsByLectureId(UUID lectureId);

    Page<CommentResponse> getCommentsPageByLectureId(
            UUID lectureId,
            Pageable pageable,
            Specification<Comment> spec
    );

    CommentResponse updateComment(UUID commentId, CommentRequest request, UUID userId);

    CommentResponse replyToComment(UUID parentCommentId, UUID userId, CommentRequest request);

    void deleteComment(UUID commentId, UUID userId);

    void voteComment(UUID commentId, UUID userId, VoteType voteType);
}
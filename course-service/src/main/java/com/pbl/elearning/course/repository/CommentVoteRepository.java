package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Comment;
import com.pbl.elearning.course.domain.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentVoteRepository extends JpaRepository<CommentVote, UUID> {
    Optional<CommentVote> findByCommentAndUserId(Comment comment, UUID userId);
}

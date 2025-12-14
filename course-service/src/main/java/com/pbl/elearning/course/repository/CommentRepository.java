package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CommentRepository
        extends JpaRepository<Comment, UUID>, JpaSpecificationExecutor<Comment> {
}

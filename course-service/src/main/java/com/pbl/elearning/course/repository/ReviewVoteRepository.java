package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Review;
import com.pbl.elearning.course.domain.ReviewVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewVoteRepository extends JpaRepository<ReviewVote, UUID> {
    Optional<ReviewVote> findByReviewAndUserId(Review review, UUID userId);

}

package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}

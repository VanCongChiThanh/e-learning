package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Review;
import com.pbl.elearning.course.payload.request.ReviewRequest;
import com.pbl.elearning.course.payload.response.ReviewResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.ReviewRepository;
import com.pbl.elearning.course.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;

    @Override
    public ReviewResponse createReview( ReviewRequest reviewRequest, UUID courseId, UUID userId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found with id: " + courseId));
        Review review= Review.builder()
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .course(course)
                .userId(userId)
                .build();
        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.fromEntity(savedReview);

    }
    @Override
    public ReviewResponse getReviewById(UUID reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException("Review not found with id: " + reviewId));
        return ReviewResponse.fromEntity(review);
    }
    @Override
    public Set<ReviewResponse> getReviewsByCourseId(UUID courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found with id: " + courseId));
        return course.getReviews().stream()
                .map(ReviewResponse::fromEntity)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public ReviewResponse updateReview(UUID reviewId, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException("Review not found with id: " + reviewId));
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        Review updatedReview = reviewRepository.save(review);
        return ReviewResponse.fromEntity(updatedReview);
    }
    @Override
    public void deleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException("Review not found with id: " + reviewId));
        reviewRepository.delete(review);
    }

    @Override
    public Double getAverageRatingByCourseId(UUID courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found with id: " + courseId));
        Double avg = reviewRepository.findAverageRatingByCourseId(courseId);
        return avg != null ? avg : 4.7;
    }

    @Override
    public Integer getTotalReviewsByCourseId(UUID courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found with id: " + courseId));
        return reviewRepository.countByCourse_CourseId(courseId);
    }

}

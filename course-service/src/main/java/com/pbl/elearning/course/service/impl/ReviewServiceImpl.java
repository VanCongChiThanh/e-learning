package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Review;
import com.pbl.elearning.course.domain.ReviewVote;
import com.pbl.elearning.course.domain.enums.VoteType;
import com.pbl.elearning.course.payload.request.ReviewRequest;
import com.pbl.elearning.course.payload.response.ReviewResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.ReviewRepository;
import com.pbl.elearning.course.repository.ReviewVoteRepository;
import com.pbl.elearning.course.service.ReviewService;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.domain.enums.Gender;
import com.pbl.elearning.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserInfoRepository userInfoRepository;
    private final ReviewVoteRepository reviewVoteRepository;

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
        UserInfo userInfo = userInfoRepository.findByUserId(review.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("UserInfo not found with userId: " + review.getUserId()));

        String userName = userInfo.getFirstName()+" "+userInfo.getLastName();
        if(userInfo.getFirstName() == null && userInfo.getLastName() == null){
            userName = "Unknown User Name";
        }
        String avatar = userInfo.getAvatar();
        if (avatar == null){
            Gender gender = userInfo.getGender();
            if (gender == Gender.MALE){
                avatar = "https://png.pngtree.com/png-vector/20230321/ourlarge/pngtree-profile-avatar-of-young-man-in-blue-shirt-and-hat-vector-png-image_6658604.png";
            } else if (gender == Gender.FEMALE) {
                avatar = "https://kynguyenlamdep.com/wp-content/uploads/2022/08/avatar-co-gai-sang-chanh.jpg";
            }

        }

        return ReviewResponse.fromEntityDetail(review, userName, avatar);
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
    public Page<ReviewResponse> getReviewsPageByCourseId(UUID courseId, Pageable pageable, Specification<Review> spec){
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found with id: " + courseId);
        }
        // 3. **THÊM ĐIỀU KIỆN MỚI**: Chỉ lấy các review gốc (parentReview IS NULL)
        Specification<Review> parentSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("parentReview"));

        // 4. Kết hợp CẢ BA Specification: từ controller, của courseId, và điều kiện mới
        Specification<Review> finalSpec = parentSpec.and(spec);

        Page<Review> reviewPage = reviewRepository.findAll(finalSpec, pageable);

        List<UUID> userIds = reviewPage.getContent().stream()
                .map(Review::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<UUID, UserInfo> userInfoMap = userInfoRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));

        return reviewPage.map(review -> {
            UserInfo userInfo = userInfoMap.get(review.getUserId());

            String userName = "Unknown User Name";
            String avatar = "https://static.vecteezy.com/system/resources/previews/009/292/244/original/default-avatar-icon-of-social-media-user-vector.jpg";

            if (userInfo != null) {
                if (userInfo.getFirstName() != null && userInfo.getLastName() != null) {
                    userName = userInfo.getFirstName() + " " + userInfo.getLastName();
                }
                avatar = userInfo.getAvatar() != null ? userInfo.getAvatar() : getDefaultAvatar(userInfo.getGender());
            }
            return ReviewResponse.fromEntityDetail(review, userName, avatar);
        });

    }


    @Override
    public ReviewResponse updateReview(UUID reviewId, ReviewRequest reviewRequest, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException("Review not found with id: " + reviewId));
        if (!review.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this review.");
        }
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        Review updatedReview = reviewRepository.save(review);
        return ReviewResponse.fromEntity(updatedReview);
    }
    @Override
    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException("Review not found with id: " + reviewId));
        if (!review.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this review.");
        }
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
    private String getDefaultAvatar(Gender gender) {
        if (gender == Gender.MALE) {
            return "https://png.pngtree.com/png-vector/20230321/ourlarge/pngtree-profile-avatar-of-young-man-in-blue-shirt-and-hat-vector-png-image_6658604.png";
        } else if (gender == Gender.FEMALE) {
            return "https://kynguyenlamdep.com/wp-content/uploads/2022/08/avatar-co-gai-sang-chanh.jpg";
        }
        return "https://static.vecteezy.com/system/resources/previews/009/292/244/original/default-avatar-icon-of-social-media-user-vector.jpg";
    }

    @Override
    public ReviewResponse replyToReview(UUID parentReviewId, UUID userId, ReviewRequest reviewRequest) {
        Review parentReview = reviewRepository.findById(parentReviewId)
                .orElseThrow(() -> new RuntimeException("Parent review not found"));

        Review reply = Review.builder()
                .rating(0)
                .comment(reviewRequest.getComment())
                .course(parentReview.getCourse())
                .userId(userId)
                .parentReview(parentReview)
                .build();

        Review savedReply = reviewRepository.save(reply);
        // Có thể trả về response chi tiết hơn nếu muốn
        return ReviewResponse.fromEntity(savedReply);
    }

    @Override
    public void voteReview(UUID reviewId, UUID userId, VoteType voteType) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));


        Optional<ReviewVote> existingVoteOpt = reviewVoteRepository.findByReviewAndUserId(review, userId);

        if (existingVoteOpt.isPresent()) {
            ReviewVote existingVote = existingVoteOpt.get();

            if (existingVote.getVoteType() == voteType) {
                reviewVoteRepository.delete(existingVote);
            } else {

                existingVote.setVoteType(voteType);
                reviewVoteRepository.save(existingVote);
            }
        } else {

            ReviewVote newVote = ReviewVote.builder()
                    .review(review)
                    .userId(userId)
                    .voteType(voteType)
                    .build();
            reviewVoteRepository.save(newVote);
        }
    }



}

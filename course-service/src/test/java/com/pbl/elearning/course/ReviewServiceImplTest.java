package com.pbl.elearning.course;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Review;
import com.pbl.elearning.course.domain.ReviewVote;
import com.pbl.elearning.course.domain.enums.VoteType;
import com.pbl.elearning.course.payload.request.ReviewRequest;
import com.pbl.elearning.course.payload.response.ReviewResponse;
import com.pbl.elearning.course.repository.*;
import com.pbl.elearning.course.service.impl.ReviewServiceImpl;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.domain.enums.Gender;
import com.pbl.elearning.user.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private ReviewVoteRepository reviewVoteRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID reviewId;
    private UUID userId;
    private Review review;

    @BeforeEach
    void setup() {
        reviewId = UUID.randomUUID();
        userId = UUID.randomUUID();

        Course course = Course.builder().courseId(UUID.randomUUID()).build();
        review = Review.builder()
                .reviewId(reviewId)
                .userId(userId)
                .comment("Good course")
                .rating(5)
                .course(course)
                .build();
    }

    // --------------------------------------------------------
    // TEST 1:
    // --------------------------------------------------------

    // --------------------------------------------------------
    // TEST 2: voteReview()
    // --------------------------------------------------------

    @Test
    void voteReview_shouldCreateNewVoteWhenNotExists() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewVoteRepository.findByReviewAndUserId(review, userId)).thenReturn(Optional.empty());

        reviewService.voteReview(reviewId, userId, VoteType.LIKE);

        ArgumentCaptor<ReviewVote> captor = ArgumentCaptor.forClass(ReviewVote.class);
        verify(reviewVoteRepository).save(captor.capture());
        ReviewVote savedVote = captor.getValue();

        assertEquals(VoteType.LIKE, savedVote.getVoteType());
        assertEquals(userId, savedVote.getUserId());
    }

    @Test
    void voteReview_shouldDeleteVoteWhenSameTypeExists() {
        ReviewVote existingVote = ReviewVote.builder()
                .review(review)
                .userId(userId)
                .voteType(VoteType.LIKE)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewVoteRepository.findByReviewAndUserId(review, userId))
                .thenReturn(Optional.of(existingVote));

        reviewService.voteReview(reviewId, userId, VoteType.LIKE);

        verify(reviewVoteRepository).delete(existingVote);
        verify(reviewVoteRepository, never()).save(any());
    }

    @Test
    void voteReview_shouldUpdateVoteWhenDifferentTypeExists() {
        ReviewVote existingVote = ReviewVote.builder()
                .review(review)
                .userId(userId)
                .voteType(VoteType.DISLIKE)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewVoteRepository.findByReviewAndUserId(review, userId))
                .thenReturn(Optional.of(existingVote));

        reviewService.voteReview(reviewId, userId, VoteType.LIKE);

        assertEquals(VoteType.LIKE, existingVote.getVoteType());
        verify(reviewVoteRepository).save(existingVote);
    }

    @Test
    void voteReview_shouldThrowExceptionWhenReviewNotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reviewService.voteReview(reviewId, userId, VoteType.LIKE));
    }
}

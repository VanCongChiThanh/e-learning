package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Comment;
import com.pbl.elearning.course.domain.CommentVote;
import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.domain.enums.VoteType;
import com.pbl.elearning.course.payload.request.CommentRequest;
import com.pbl.elearning.course.payload.response.CommentResponse;
import com.pbl.elearning.course.repository.CommentRepository;
import com.pbl.elearning.course.repository.CommentVoteRepository;
import com.pbl.elearning.course.repository.LectureRepository;
import com.pbl.elearning.course.service.CommentService;
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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final LectureRepository lectureRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public CommentResponse createComment(UUID lectureId, UUID userId, CommentRequest request) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        Comment comment = Comment.builder()
                .lecture(lecture)
                .userId(userId)
                .content(request.getContent())
                .build();

        // Khi mới tạo, trả về entity basic
        return CommentResponse.fromEntity(commentRepository.save(comment));
    }

    @Override
    public CommentResponse getCommentById(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        UserInfo userInfo = userInfoRepository.findByUserId(comment.getUserId())
                .orElseThrow(() -> new RuntimeException("UserInfo not found"));

        return CommentResponse.fromEntityDetail(
                comment,
                getUserName(userInfo),
                getAvatar(userInfo)
        );
    }

    @Override
    public Set<CommentResponse> getCommentsByLectureId(UUID lectureId) {
        if (!lectureRepository.existsById(lectureId)) {
            throw new RuntimeException("Lecture not found");
        }

        Specification<Comment> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("lecture").get("lectureId"), lectureId),
                cb.isNull(root.get("parentComment")) // Chỉ lấy comment gốc
        );

        List<Comment> comments = commentRepository.findAll(spec);
        Set<CommentResponse> responses = new HashSet<>();

        for (Comment comment : comments) {
            UserInfo userInfo = userInfoRepository.findByUserId(comment.getUserId())
                    .orElse(null);

            responses.add(CommentResponse.fromEntityDetail(
                    comment,
                    getUserName(userInfo),
                    getAvatar(userInfo)
            ));
        }
        return responses;
    }

    @Override
    public Page<CommentResponse> getCommentsPageByLectureId(
            UUID lectureId,
            Pageable pageable,
            Specification<Comment> spec
    ) {
        if (!lectureRepository.existsById(lectureId)) {
            throw new RuntimeException("Lecture not found");
        }

        // Chỉ lấy các comment gốc (parentComment IS NULL)
        Specification<Comment> parentSpec = (root, query, cb) -> cb.isNull(root.get("parentComment"));
        Specification<Comment> lectureSpec = (root, query, cb) -> cb.equal(root.get("lecture").get("lectureId"), lectureId);

        Specification<Comment> finalSpec = lectureSpec.and(parentSpec).and(spec);

        Page<Comment> commentPage = commentRepository.findAll(finalSpec, pageable);

        // Tối ưu: Lấy danh sách userIds để query 1 lần (Batch fetching)
        List<UUID> userIds = commentPage.getContent().stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<UUID, UserInfo> userInfoMap = userInfoRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));

        return commentPage.map(comment -> {
            UserInfo userInfo = userInfoMap.get(comment.getUserId());
            return CommentResponse.fromEntityDetail(
                    comment,
                    getUserName(userInfo),
                    getAvatar(userInfo)
            );
        });
    }

    @Override
    public CommentResponse updateComment(UUID commentId, CommentRequest request, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this comment.");
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);

        // Trả về response simple vì người dùng vừa sửa xong, đã biết thông tin của mình
        return CommentResponse.fromEntity(updatedComment);
    }

    @Override
    public CommentResponse replyToComment(UUID parentCommentId, UUID userId, CommentRequest request) {
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Comment reply = Comment.builder()
                .lecture(parent.getLecture())
                .userId(userId)
                .content(request.getContent())
                .parentComment(parent)
                .build();

        return CommentResponse.fromEntity(commentRepository.save(reply));
    }

    @Override
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this comment.");
        }

        commentRepository.delete(comment);
    }

    @Override
    public void voteComment(UUID commentId, UUID userId, VoteType voteType) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<CommentVote> existingVote =
                commentVoteRepository.findByCommentAndUserId(comment, userId);

        if (existingVote.isPresent()) {
            CommentVote vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                // Nếu vote lại cùng loại -> Hủy vote (Toggle)
                commentVoteRepository.delete(vote);
            } else {
                // Đổi loại vote (Like -> Dislike hoặc ngược lại)
                vote.setVoteType(voteType);
                commentVoteRepository.save(vote);
            }
        } else {
            // Tạo vote mới
            commentVoteRepository.save(CommentVote.builder()
                    .comment(comment)
                    .userId(userId)
                    .voteType(voteType)
                    .build());
        }
    }

    // --- Helper Methods (Logic giống ReviewServiceImpl) ---

    private String getUserName(UserInfo userInfo) {
        if (userInfo != null && userInfo.getFirstName() != null && userInfo.getLastName() != null) {
            return userInfo.getFirstName() + " " + userInfo.getLastName();
        }
        return "Unknown User Name";
    }

    private String getAvatar(UserInfo userInfo) {
        if (userInfo != null && userInfo.getAvatar() != null) {
            return userInfo.getAvatar();
        }
        return getDefaultAvatar(userInfo != null ? userInfo.getGender() : null);
    }

    private String getDefaultAvatar(Gender gender) {
        if (gender == Gender.MALE) {
            return "https://png.pngtree.com/png-vector/20230321/ourlarge/pngtree-profile-avatar-of-young-man-in-blue-shirt-and-hat-vector-png-image_6658604.png";
        } else if (gender == Gender.FEMALE) {
            return "https://kynguyenlamdep.com/wp-content/uploads/2022/08/avatar-co-gai-sang-chanh.jpg";
        }
        // Default avatar
        return "https://static.vecteezy.com/system/resources/previews/009/292/244/original/default-avatar-icon-of-social-media-user-vector.jpg";
    }
}
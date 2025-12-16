package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.commerce.PagingUtils;
import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.PageInfo;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.domain.Comment;
import com.pbl.elearning.course.domain.enums.VoteType;
import com.pbl.elearning.course.payload.request.CommentRequest;
import com.pbl.elearning.course.payload.response.CommentResponse;
import com.pbl.elearning.course.service.CommentService;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/lectures/{lectureId}/comments")
public class CommentController {

    private final CommentService commentService;

    /* ================= CREATE COMMENT ================= */
    @PostMapping
    @PreAuthorize("hasAnyRole('LEARNER','INSTRUCTOR','ADMIN')")
    public ResponseEntity<ResponseDataAPI> create(
            @PathVariable UUID lectureId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        return ResponseEntity.ok(
                ResponseDataAPI.success(
                        commentService.createComment(lectureId, userId, request),
                        "Comment created successfully"
                )
        );
    }

    /* ================= GET COMMENT BY ID ================= */
    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseDataAPI> getCommentById(@PathVariable UUID commentId) {
        return ResponseEntity.ok(
                ResponseDataAPI.success(
                        commentService.getCommentById(commentId),
                        CommonConstant.SUCCESS
                )
        );
    }

    /* ================= GET ALL COMMENTS (NO PAGING) ================= */
    @GetMapping
    public ResponseEntity<ResponseDataAPI> getCommentsByLecture(@PathVariable UUID lectureId) {
        Set<CommentResponse> comments = commentService.getCommentsByLectureId(lectureId);
        return ResponseEntity.ok(
                ResponseDataAPI.success(comments, CommonConstant.SUCCESS)
        );
    }

    /* ================= GET COMMENTS (PAGING + FILTER) ================= */
    @GetMapping("/Page")
    public ResponseEntity<ResponseDataAPI> getCommentsPageByLecture(
            @PathVariable UUID lectureId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "paging", defaultValue = "5") int paging,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @Filter Specification<Comment> specification
    ) {
        Pageable pageable = PagingUtils.makePageRequest(sort, order, page, paging);

        Page<CommentResponse> commentPage = commentService.getCommentsPageByLectureId(lectureId, pageable, specification);

        PageInfo pageInfo = new PageInfo(
                page,
                commentPage.getTotalPages(),
                commentPage.getTotalElements()
        );

        return ResponseEntity.ok(ResponseDataAPI.success(commentPage.getContent(), pageInfo));
    }

    /* ================= UPDATE COMMENT ================= */
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('LEARNER','INSTRUCTOR','ADMIN')")
    public ResponseEntity<ResponseDataAPI> update(
            @PathVariable UUID commentId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        return ResponseEntity.ok(
                ResponseDataAPI.success(
                        commentService.updateComment(commentId, request, userId),
                        "Comment updated successfully"
                )
        );
    }

    /* ================= DELETE COMMENT ================= */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('LEARNER','INSTRUCTOR','ADMIN')")
    public ResponseEntity<ResponseDataAPI> delete(
            @PathVariable UUID commentId,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(
                ResponseDataAPI.success(null, "Comment deleted successfully")
        );
    }

    /* ================= REPLY COMMENT ================= */
    @PostMapping("/{commentId}/reply")
    @PreAuthorize("hasAnyRole('LEARNER','INSTRUCTOR','ADMIN')")
    public ResponseEntity<ResponseDataAPI> reply(
            @PathVariable UUID commentId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        return ResponseEntity.ok(
                ResponseDataAPI.success(
                        commentService.replyToComment(commentId, userId, request),
                        "Replied successfully"
                )
        );
    }

    /* ================= VOTE COMMENT ================= */
    @PostMapping("/{commentId}/vote")
    @PreAuthorize("hasAnyRole('LEARNER','INSTRUCTOR','ADMIN')")
    public ResponseEntity<ResponseDataAPI> vote(
            @PathVariable UUID commentId,
            @RequestParam VoteType voteType,
            Authentication authentication
    ) {
        UUID userId = getUserId(authentication);
        commentService.voteComment(commentId, userId, voteType);
        return ResponseEntity.ok(
                ResponseDataAPI.success(null, "Voted successfully")
        );
    }

    /* ================= UTILITY ================= */
    private UUID getUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        return ((UserPrincipal) authentication.getPrincipal()).getId();
    }
}
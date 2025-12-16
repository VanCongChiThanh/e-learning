package com.pbl.elearning.course.domain;

import com.pbl.elearning.course.domain.enums.VoteType;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "comment_votes",
        uniqueConstraints = {
                // mỗi user chỉ được vote 1 lần cho 1 comment
                @UniqueConstraint(columnNames = {"comment_id", "user_id"})
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentVote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID commentVoteId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType; // LIKE, DISLIKE
}

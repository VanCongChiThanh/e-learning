package com.pbl.elearning.course.domain;

import com.pbl.elearning.course.domain.enums.VoteType;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "review_votes", uniqueConstraints = {
        // Đảm bảo mỗi user chỉ có thể vote 1 lần cho 1 review
        @UniqueConstraint(columnNames = {"review_id", "user_id"})
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID reviewVoteId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;
}

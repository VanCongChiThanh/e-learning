package com.pbl.elearning.course.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Table(name = "reviews")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "courseId")
    private Course course;


    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewVote> votes;


    @Formula("(SELECT COUNT(*) FROM review_votes v WHERE v.review_id = review_id AND v.vote_type = 'LIKE')")
    private int likeCount;


    @Formula("(SELECT COUNT(*) FROM review_votes v WHERE v.review_id = review_id AND v.vote_type = 'DISLIKE')")
    private int dislikeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_review_id")
    private Review parentReview;

    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL, fetch = FetchType.EAGER) // EAGER để dễ lấy replies
    private Set<Review> replies = new HashSet<>();





}

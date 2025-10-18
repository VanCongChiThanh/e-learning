package com.pbl.elearning.course.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.*;

import javax.persistence.*;
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





}

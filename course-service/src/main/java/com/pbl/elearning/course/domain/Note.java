package com.pbl.elearning.course.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Table(name = "notes")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID noteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lectureId")
    private Lecture lecture;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


}

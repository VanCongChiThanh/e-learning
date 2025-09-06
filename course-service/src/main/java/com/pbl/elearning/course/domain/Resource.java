package com.pbl.elearning.course.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Table(name = "resources")
@Entity
@Builder
public class Resource extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID resourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(name = "fileUrl", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "fileType", nullable = false, length = 50)
    private String fileType;





}

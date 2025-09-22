package com.pbl.elearning.course.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.course.domain.enums.LectureType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.codec.AbstractEncoder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "lectures")
@Builder
public class Lecture extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID lectureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionId")
    private  Section section;

    @Column(nullable = false)
    private String title;

    @Column(name = "sourceUrl", columnDefinition = "TEXT")
    private String sourceUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LectureType type;

    private Integer duration;
    private Integer position = 0;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Resource> resources= new HashSet<>();



}

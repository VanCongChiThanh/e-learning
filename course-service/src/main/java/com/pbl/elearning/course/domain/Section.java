package com.pbl.elearning.course.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "sections")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID sectionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="courseId")
    private Course course;

    @Column(nullable = false)
    private String title;

    private  Integer position=0;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lecture> lectures = new HashSet<>();



}

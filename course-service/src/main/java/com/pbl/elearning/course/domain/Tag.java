package com.pbl.elearning.course.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Table(name = "tags")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID tagId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Course> courses = new HashSet<>();


}

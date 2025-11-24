package com.pbl.elearning.course.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "course_vector")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class CourseVector extends AbstractEntity {

    @Id
    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Type(type = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "jsonb")
    private List<Float> embedding;

}
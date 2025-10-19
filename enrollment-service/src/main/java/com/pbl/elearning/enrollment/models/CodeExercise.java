package com.pbl.elearning.enrollment.models;

import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.course.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "code_exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExercise extends AbstractEntity {
    @Id
    @GeneratedValue
    private UUID codeExerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String problemStatement; // Đề bài chi tiết

    private Integer timeLimitSeconds; // Thời gian giới hạn (tính bằng giây)


    // Một bài tập code sẽ có nhiều test case
    @OneToMany(
            mappedBy = "codeExercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CodeTestCase> testCases;
}

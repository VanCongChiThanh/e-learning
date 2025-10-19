package com.pbl.elearning.enrollment.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "code_test_cases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeTestCase {
    @Id
    @GeneratedValue
    private UUID codeTestCaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private CodeExercise codeExercise;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String inputData;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;

    private Integer points;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isHidden = false;

    private Integer sortOrder;
}

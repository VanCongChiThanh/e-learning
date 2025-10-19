package com.pbl.elearning.enrollment.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl.elearning.enrollment.models.CodeExercise;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeExerciseResponse {
    private UUID id;
    private UUID lectureId;
    private String title;
    private String problemStatement;
    private Integer timeLimitSeconds;
    private Timestamp createdAt;
    private List<CodeTestCaseResponse> testCases;

    public static CodeExerciseResponse fromEntity(CodeExercise exercise) {
        List<CodeTestCaseResponse> testCaseResponses = null;
        if (exercise.getTestCases() != null && !exercise.getTestCases().isEmpty()) {
            testCaseResponses = exercise.getTestCases().stream()
                    .map(CodeTestCaseResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return CodeExerciseResponse.builder()
                .id(exercise.getCodeExerciseId())
                .lectureId(exercise.getLecture().getLectureId()) // Assuming Lecture has a getId() method
                .title(exercise.getTitle())
                .problemStatement(exercise.getProblemStatement())
                .timeLimitSeconds(exercise.getTimeLimitSeconds())
                .createdAt(exercise.getCreatedAt())
                .testCases(testCaseResponses)
                .build();
    }
    public static CodeExerciseResponse fromEntitySimple(CodeExercise exercise){
        return CodeExerciseResponse.builder()
                .id(exercise.getCodeExerciseId())
                .title(exercise.getTitle())
                .build();
    }
}

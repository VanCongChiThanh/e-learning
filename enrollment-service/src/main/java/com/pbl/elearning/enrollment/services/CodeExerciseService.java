package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.response.CodeExerciseResponse;

import java.util.List;
import java.util.UUID;

public interface CodeExerciseService {
    CodeExerciseResponse getCodeExerciseById(UUID exerciseId);
    List<CodeExerciseResponse> getAllCodeExercisesByLectureId(UUID lectureId);
}

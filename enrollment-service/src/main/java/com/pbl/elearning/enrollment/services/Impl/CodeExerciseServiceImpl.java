package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.CodeExercise;
import com.pbl.elearning.enrollment.payload.response.CodeExerciseResponse;
import com.pbl.elearning.enrollment.repository.CodeExerciseRepository;
import com.pbl.elearning.enrollment.services.CodeExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeExerciseServiceImpl implements CodeExerciseService {
    private final CodeExerciseRepository codeExerciseRepository;

    @Override
    public CodeExerciseResponse getCodeExerciseById(UUID exerciseId) {
        CodeExercise exercise = codeExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("CodeExercise not found with id: " + exerciseId));

        return CodeExerciseResponse.fromEntity(exercise);
    }

    @Override
    public List<CodeExerciseResponse> getAllCodeExercisesByLectureId(UUID lectureId) {
        List<CodeExercise> exercises = codeExerciseRepository.findAllByLecture_LectureId(lectureId);

        return exercises.stream()
                .map(CodeExerciseResponse::fromEntitySimple)
                .collect(Collectors.toList());
    }
}

package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.enrollment.payload.response.CodeExerciseResponse;
import com.pbl.elearning.enrollment.services.CodeExerciseService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/code-exercises")
@RequiredArgsConstructor
public class CodeExerciseController {
    private final CodeExerciseService codeExerciseService;

    @GetMapping("/{exerciseId}")
    public ResponseEntity<ResponseDataAPI> getCodeExerciseById(@PathVariable UUID exerciseId) {
        CodeExerciseResponse response = codeExerciseService.getCodeExerciseById(exerciseId);
        return ResponseEntity.ok(ResponseDataAPI.success(response, "Code exercise retrieved successfully"));
    }

    @GetMapping("/by-lecture/{lectureId}")
    public ResponseEntity<ResponseDataAPI> getAllCodeExercisesByLectureId(@PathVariable UUID lectureId) {
        List<CodeExerciseResponse> response = codeExerciseService.getAllCodeExercisesByLectureId(lectureId);
        return ResponseEntity.ok(ResponseDataAPI.success(response, "Code exercises for lecture retrieved successfully"));
    }
}

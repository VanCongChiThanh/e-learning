package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.CodeSubmission;
import com.pbl.elearning.enrollment.payload.response.CodeSubmissionResponse;
import com.pbl.elearning.enrollment.repository.CodeSubmissionRepository;
import com.pbl.elearning.enrollment.services.CodeSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeSubmissionServiceImpl implements CodeSubmissionService {

    private final CodeSubmissionRepository codeSubmissionRepository;

    @Override
    public Page<CodeSubmissionResponse> getSubmissionsByExerciseAndUser(
            UUID exerciseId,
            UUID userId,
            Pageable pageable,
            Specification<CodeSubmission> spec
    ) {
        // 1. Tạo điều kiện bắt buộc: Phải đúng ExerciseID và UserID
        Specification<CodeSubmission> baseSpec = (root, query, cb) -> cb.and(
                cb.equal(root.get("exerciseId"), exerciseId),
                cb.equal(root.get("userId"), userId)
        );

        // 2. Kết hợp với spec từ controller (nếu FE muốn lọc thêm status, languageId...)
        Specification<CodeSubmission> finalSpec = Specification.where(baseSpec).and(spec);

        // 3. Query và Map sang DTO
        return codeSubmissionRepository.findAll(finalSpec, pageable)
                .map(CodeSubmissionResponse::fromEntity);
    }

    @Override
    public boolean hasUserSubmitted(UUID exerciseId, UUID userId) {
        return codeSubmissionRepository.existsByExerciseIdAndUserId(exerciseId, userId);
    }
}
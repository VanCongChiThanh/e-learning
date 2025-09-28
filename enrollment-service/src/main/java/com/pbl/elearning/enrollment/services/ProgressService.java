package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.models.Progress;
import com.pbl.elearning.enrollment.payload.request.CreateProgressRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateProgressRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgressService {

    // Tạo mới tiến trình cho lecture
    Progress createProgress(CreateProgressRequest request);

    // Cập nhật tiến trình đã tồn tại
    Progress updateProgress(UUID progressId, UpdateProgressRequest request);

    // Lấy tiến trình theo id
    Optional<Progress> getProgressById(UUID progressId);

    // Lấy tất cả tiến trình theo enrollment
    List<Progress> getProgressByEnrollmentId(UUID enrollmentId);
    List<Progress> getProgressByLectureId(UUID lectureId);
}

package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;
import com.pbl.elearning.enrollment.payload.response.EnrollmentReportResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentService {

    /**
     * Create new enrollment
     *
     * @param request EnrollmentRequest
     * @return Enrollment
     */
    Enrollment createEnrollment(EnrollmentRequest request);

    /**
     * Delete enrollment
     *
     * @param id Enrollment id
     */
    void deleteEnrollment(UUID id);

    /**
     * Get enrollment by id
     *
     * @param id Enrollment id
     * @return Optional<Enrollment>
     */
    Optional<Enrollment> getEnrollmentById(UUID id);

    /**
     * Get all enrollments
     *
     * @return List<Enrollment>
     */
    List<Enrollment> getAllEnrollments();

    /**
     * Get enrollments by user id
     *
     * @param userId User id
     * @return List<Enrollment>
     */
    List<Enrollment> getEnrollmentsByUserId(UUID userId);

    /**
     * Get enrollments by course id
     *
     * @param courseId Course id
     * @return List<Enrollment>
     */
    List<Enrollment> getEnrollmentsByCourseId(UUID courseId);

    /**
     * Get detailed enrollment report by enrollment id
     *
     * @param enrollmentId Enrollment id
     * @return EnrollmentReportResponse
     */
    EnrollmentReportResponse getEnrollmentReport(UUID enrollmentId);

    /**
     * Get enrollment reports by course id
     *
     * @param courseId Course id
     * @return List<EnrollmentReportResponse>
     */
    List<EnrollmentReportResponse> getEnrollmentReportsByCourse(UUID courseId);

    /**
     * Get enrollment reports by user id
     *
     * @param userId User id
     * @return List<EnrollmentReportResponse>
     */
    List<EnrollmentReportResponse> getEnrollmentReportsByUser(UUID userId);

    Boolean checkExistsByUserId(UUID userId, UUID courseId);
}

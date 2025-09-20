package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateEnrollmentRequest;
import com.pbl.elearning.enrollment.repository.EnrollmentRepository;
import com.pbl.elearning.enrollment.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Enrollment createEnrollment(EnrollmentRequest request) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(request.getUserId());
        enrollment.setCourseId(request.getCourseId());
        enrollment.setEnrollmentDate(OffsetDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment updateEnrollment(UUID id, UpdateEnrollmentRequest request) {
        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findById(id);
        if (optionalEnrollment.isPresent()) {
            Enrollment enrollment = optionalEnrollment.get();
            enrollment.setProgressPercentage(request.getProgressPercentage());
            enrollment.setStatus(request.getStatus());
            enrollment.setCompletionDate(request.getCompletionDate());
            enrollment.setTotalWatchTimeMinutes(request.getTotalWatchTimeMinutes());
            enrollment.setLastAccessedAt(request.getLastAccessedAt());
            enrollment.setUpdatedAt(OffsetDateTime.now());
            return enrollmentRepository.save(enrollment);
        }
        return null;
    }

    @Override
    public void deleteEnrollment(UUID id) {
        enrollmentRepository.deleteById(id);
    }

    @Override
    public Optional<Enrollment> getEnrollmentById(UUID id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> getEnrollmentsByUserId(UUID userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourseId(UUID courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }
}

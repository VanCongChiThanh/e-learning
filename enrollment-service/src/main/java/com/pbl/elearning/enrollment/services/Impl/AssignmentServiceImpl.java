package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.enums.AssignmentStatus;
import com.pbl.elearning.enrollment.models.Assignment;
import com.pbl.elearning.enrollment.payload.request.AssignmentRequest;
import com.pbl.elearning.enrollment.payload.response.AssignmentResponse;
import com.pbl.elearning.enrollment.repository.AssignmentRepository;
import com.pbl.elearning.enrollment.services.AssignmentService;
import com.pbl.elearning.course.domain.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository repository;

    private AssignmentResponse mapToResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .courseId(assignment.getCourse() != null ? assignment.getCourse().getCourseId() : null)
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .status(assignment.getStatus())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        System.out.println("request " + request);

        Course course = Course.builder()
                .courseId(request.getCourseId())
                .build();

        Assignment assignment = Assignment.builder()
                .course(course)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(AssignmentStatus.ACTIVE)
                .createdAt(OffsetDateTime.now())
                .build();

        Assignment saved = repository.save(assignment);
        System.out.println("Dax luu" + saved);
        return mapToResponse(saved);
    }

    @Override
    public AssignmentResponse getAssignmentById(UUID id) {
        Assignment assignment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));
        return mapToResponse(assignment);
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByCourseId(UUID courseId) {
        return repository.findByCourse_CourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentResponse updateAssignment(UUID id, AssignmentRequest request) {
        Assignment assignment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));

        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());

        Assignment updated = repository.save(assignment);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAssignment(UUID id) {
        repository.deleteById(id);
    }
}
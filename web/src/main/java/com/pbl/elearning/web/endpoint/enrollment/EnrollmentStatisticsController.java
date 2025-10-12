package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.payload.response.EnrollmentReportResponse;
import com.pbl.elearning.enrollment.services.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/enrollment-statistics")
@RequiredArgsConstructor
public class EnrollmentStatisticsController {

    private final EnrollmentService enrollmentService;

    /**
     * Get overall course statistics
     */
    @GetMapping("/course/{courseId}/overview")
    public ResponseEntity<Map<String, Object>> getCourseStatistics(@PathVariable UUID courseId) {
        List<EnrollmentReportResponse> reports = enrollmentService.getEnrollmentReportsByCourse(courseId);
        
        Map<String, Object> statistics = Map.of(
            "totalEnrollments", reports.size(),
            "completedEnrollments", reports.stream()
                .mapToLong(r -> "COMPLETED".equals(r.getEnrollmentStatus()) ? 1 : 0)
                .sum(),
            "averageProgress", reports.stream()
                .mapToDouble(r -> r.getProgressPercentage() != null ? r.getProgressPercentage() : 0.0)
                .average()
                .orElse(0.0),
            "totalWatchTime", reports.stream()
                .mapToInt(r -> r.getTotalWatchTimeMinutes() != null ? r.getTotalWatchTimeMinutes() : 0)
                .sum(),
            "averageQuizScore", reports.stream()
                .mapToDouble(r -> r.getAverageQuizScore() != null ? r.getAverageQuizScore() : 0.0)
                .average()
                .orElse(0.0),
            "averageAssignmentScore", reports.stream()
                .mapToDouble(r -> r.getAverageAssignmentScore() != null ? r.getAverageAssignmentScore() : 0.0)
                .average()
                .orElse(0.0),
            "certificatesIssued", reports.stream()
                .mapToLong(r -> r.getHasCertificate() != null && r.getHasCertificate() ? 1 : 0)
                .sum()
        );
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get user's learning statistics across all courses
     */
    @GetMapping("/user/{userId}/overview")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable UUID userId) {
        List<EnrollmentReportResponse> reports = enrollmentService.getEnrollmentReportsByUser(userId);
        
        Map<String, Object> statistics = Map.of(
            "totalEnrollments", reports.size(),
            "completedCourses", reports.stream()
                .mapToLong(r -> "COMPLETED".equals(r.getEnrollmentStatus()) ? 1 : 0)
                .sum(),
            "inProgressCourses", reports.stream()
                .mapToLong(r -> "ACTIVE".equals(r.getEnrollmentStatus()) ? 1 : 0)
                .sum(),
            "totalWatchTime", reports.stream()
                .mapToInt(r -> r.getTotalWatchTimeMinutes() != null ? r.getTotalWatchTimeMinutes() : 0)
                .sum(),
            "totalQuizzesCompleted", reports.stream()
                .mapToInt(r -> r.getCompletedQuizzes() != null ? r.getCompletedQuizzes() : 0)
                .sum(),
            "totalQuizzesPassed", reports.stream()
                .mapToInt(r -> r.getPassedQuizzes() != null ? r.getPassedQuizzes() : 0)
                .sum(),
            "totalAssignmentsSubmitted", reports.stream()
                .mapToInt(r -> r.getSubmittedAssignments() != null ? r.getSubmittedAssignments() : 0)
                .sum(),
            "certificatesEarned", reports.stream()
                .mapToLong(r -> r.getHasCertificate() != null && r.getHasCertificate() ? 1 : 0)
                .sum()
        );
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get course completion trends
     */
    @GetMapping("/course/{courseId}/completion-trends")
    public ResponseEntity<Map<String, Object>> getCourseCompletionTrends(@PathVariable UUID courseId) {
        List<EnrollmentReportResponse> reports = enrollmentService.getEnrollmentReportsByCourse(courseId);
        
        // Group by completion status
        Map<String, Long> completionByStatus = reports.stream()
            .collect(Collectors.groupingBy(
                r -> r.getEnrollmentStatus() != null ? r.getEnrollmentStatus() : "UNKNOWN",
                Collectors.counting()
            ));

        // Progress distribution
        Map<String, Long> progressDistribution = reports.stream()
            .collect(Collectors.groupingBy(
                r -> {
                    Double progress = r.getProgressPercentage();
                    if (progress == null || progress == 0) return "0%";
                    if (progress < 25) return "1-24%";
                    if (progress < 50) return "25-49%";
                    if (progress < 75) return "50-74%";
                    if (progress < 100) return "75-99%";
                    return "100%";
                },
                Collectors.counting()
            ));

        Map<String, Object> trends = Map.of(
            "completionByStatus", completionByStatus,
            "progressDistribution", progressDistribution
        );
        
        return ResponseEntity.ok(trends);
    }

    /**
     * Get quiz performance statistics for a course
     */
    @GetMapping("/course/{courseId}/quiz-performance")
    public ResponseEntity<Map<String, Object>> getQuizPerformanceStatistics(@PathVariable UUID courseId) {
        List<EnrollmentReportResponse> reports = enrollmentService.getEnrollmentReportsByCourse(courseId);
        
        Map<String, Object> quizStats = Map.of(
            "totalStudents", reports.size(),
            "studentsWithQuizzes", reports.stream()
                .mapToLong(r -> r.getCompletedQuizzes() != null && r.getCompletedQuizzes() > 0 ? 1 : 0)
                .sum(),
            "averageQuizScore", reports.stream()
                .filter(r -> r.getAverageQuizScore() != null)
                .mapToDouble(EnrollmentReportResponse::getAverageQuizScore)
                .average()
                .orElse(0.0),
            "totalQuizzesCompleted", reports.stream()
                .mapToInt(r -> r.getCompletedQuizzes() != null ? r.getCompletedQuizzes() : 0)
                .sum(),
            "totalQuizzesPassed", reports.stream()
                .mapToInt(r -> r.getPassedQuizzes() != null ? r.getPassedQuizzes() : 0)
                .sum(),
            "passRate", calculatePassRate(reports)
        );
        
        return ResponseEntity.ok(quizStats);
    }

    private double calculatePassRate(List<EnrollmentReportResponse> reports) {
        int totalCompleted = reports.stream()
            .mapToInt(r -> r.getCompletedQuizzes() != null ? r.getCompletedQuizzes() : 0)
            .sum();
        
        int totalPassed = reports.stream()
            .mapToInt(r -> r.getPassedQuizzes() != null ? r.getPassedQuizzes() : 0)
            .sum();
        
        return totalCompleted > 0 ? (double) totalPassed / totalCompleted * 100 : 0.0;
    }
}
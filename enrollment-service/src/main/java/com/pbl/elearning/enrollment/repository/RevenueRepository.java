package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Repository
public interface RevenueRepository extends JpaRepository<Enrollment, UUID> {

    @Query(value = """
    SELECT
        CAST(u.id AS TEXT) AS instructor_id,
        CONCAT(ui.first_name, ' ', ui.last_name) AS instructor_name,
        ui.avatar,

        COUNT(DISTINCT c.course_id) AS total_courses,

        COALESCE(SUM(c.price * ec.enrolled), 0) AS total_revenue,

        0.7 AS commission_percentage,
        COALESCE(SUM(c.price * ec.enrolled), 0) * 0.7 AS net_earnings

    FROM users u
    LEFT JOIN user_info ui
        ON ui.user_id = u.id
    LEFT JOIN courses c
        ON c.instructor_id = u.id
    LEFT JOIN (
        SELECT course_id, COUNT(*) AS enrolled
        FROM enrollments
        WHERE enrollment_date BETWEEN :startDate AND :endDate
        GROUP BY course_id
    ) ec
        ON ec.course_id = c.course_id

    WHERE u.id = :instructorId
      AND u.role = 'ROLE_INSTRUCTOR'

    GROUP BY u.id, ui.first_name, ui.last_name, ui.avatar
""", nativeQuery = true)
    List<Object[]>getInstructorRevenueRaw(UUID instructorId,
                                     Instant startDate,
                                     Instant endDate);

    @Query(value = """
    SELECT
        CAST(u.id AS TEXT) AS instructor_id,
        CONCAT(ui.first_name, ' ', ui.last_name) AS instructor_name,
        ui.avatar,

        COUNT(DISTINCT c.course_id) AS total_courses,

        COALESCE(SUM(c.price * ec.enrolled), 0) AS total_revenue,

        0.7 AS commission_percentage,
        COALESCE(SUM(c.price * ec.enrolled), 0) * 0.7 AS net_earnings

    FROM users u
    LEFT JOIN user_info ui
        ON ui.user_id = u.id
    LEFT JOIN courses c
        ON c.instructor_id = u.id
    LEFT JOIN (
        SELECT course_id, COUNT(*) AS enrolled
        FROM enrollments
        WHERE enrollment_date BETWEEN :startDate AND :endDate
        GROUP BY course_id
    ) ec
        ON ec.course_id = c.course_id

    WHERE u.role = 'ROLE_INSTRUCTOR'

    GROUP BY u.id, ui.first_name, ui.last_name, ui.avatar
    ORDER BY net_earnings DESC
""", nativeQuery = true)
    List<Object[]> getAllInstructorRevenueRaw(Instant startDate,
                                              Instant endDate);
}
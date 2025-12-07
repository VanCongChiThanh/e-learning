package com.pbl.elearning.commerce.repository;

import com.pbl.elearning.commerce.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Repository
public interface RevenueRepository extends JpaRepository<Order, UUID> {
    @Query(value = """
    SELECT
        CAST(u.id AS TEXT) AS instructor_id,
        CONCAT(ui.first_name, ' ', ui.last_name) AS instructor_name,
        ui.avatar,

        COUNT(DISTINCT c.course_id) AS total_courses,

        COALESCE(SUM(oc.revenue), 0) AS total_revenue,
        0.7 AS commission_percentage,
        COALESCE(SUM(oc.revenue) * 0.7, 0) AS net_earnings

    FROM users u
    LEFT JOIN user_info ui
        ON ui.user_id = u.id

    LEFT JOIN courses c
        ON c.instructor_id = u.id

    LEFT JOIN (
        SELECT 
            oi.course_id,
            SUM(oi.unit_price) AS revenue
        FROM order_items oi
        JOIN orders o 
            ON o.id = oi.order_id
           AND o.status = 'PAID'
           AND o.created_at BETWEEN :startDate AND :endDate
        GROUP BY oi.course_id
    ) oc ON oc.course_id = c.course_id

    WHERE u.id = :instructorId
      AND u.role = 'ROLE_INSTRUCTOR'

    GROUP BY u.id, ui.first_name, ui.last_name, ui.avatar
""", nativeQuery = true)
    List<Object[]> getInstructorRevenueRaw(
            UUID instructorId,
            Instant startDate,
            Instant endDate
    );
    @Query(value = """
    SELECT
        CAST(u.id AS TEXT) AS instructor_id,
        CONCAT(ui.first_name, ' ', ui.last_name) AS instructor_name,
        ui.avatar,

        COUNT(DISTINCT c.course_id) AS total_courses,

        COALESCE(SUM(oc.revenue), 0) AS total_revenue,
        0.7 AS commission_percentage,
        COALESCE(SUM(oc.revenue) * 0.7, 0) AS net_earnings

    FROM users u
    LEFT JOIN user_info ui
        ON ui.user_id = u.id

    LEFT JOIN courses c
        ON c.instructor_id = u.id
    LEFT JOIN (
        SELECT 
            oi.course_id,
            SUM(oi.unit_price) AS revenue
        FROM order_items oi
        JOIN orders o 
            ON o.id = oi.order_id
           AND o.status = 'PAID'
           AND o.created_at BETWEEN :startDate AND :endDate
        GROUP BY oi.course_id
    ) oc ON oc.course_id = c.course_id

    WHERE u.role = 'ROLE_INSTRUCTOR'

    GROUP BY u.id, ui.first_name, ui.last_name, ui.avatar
    ORDER BY net_earnings DESC
""", nativeQuery = true)
    List<Object[]> getAllInstructorRevenueRaw(
            Instant startDate,
            Instant endDate
    );


    @Query(value = """
    SELECT
        CAST(c.course_id AS TEXT) AS course_id,
        c.title,
        c.price,
        COALESCE(oc.total_sales, 0) AS total_sales,
        COALESCE(oc.gross_revenue, 0) AS gross_revenue,
        COALESCE(oc.net_earnings, 0) AS net_earnings
    FROM courses c
    LEFT JOIN (
        SELECT 
            oi.course_id,
            COUNT(oi.id) AS total_sales,
            SUM(oi.unit_price) AS gross_revenue,
            SUM(oi.unit_price) * 0.7 AS net_earnings
        FROM order_items oi
        JOIN orders o
            ON o.id = oi.order_id
           AND o.status = 'PAID'
           AND o.created_at BETWEEN :startDate AND :endDate
        GROUP BY oi.course_id
    ) oc ON oc.course_id = c.course_id
    WHERE c.instructor_id = :instructorId
    ORDER BY gross_revenue DESC
""", nativeQuery = true)
    List<Object[]> getInstructorCourseRevenue(
            UUID instructorId,
            Instant startDate,
            Instant endDate
    );

    @Query(value = """
    SELECT
        CAST(o.id AS TEXT) AS order_id,
        u.email AS student_email,
        oi.unit_price AS gross,
        oi.unit_price * 0.7 AS net,
        o.created_at
    FROM order_items oi
    JOIN orders o 
        ON o.id = oi.order_id 
        AND o.status = 'PAID'
        AND o.created_at BETWEEN :start AND :end
    JOIN users u ON u.id = o.user_id
    WHERE oi.course_id = :courseId
    ORDER BY o.created_at DESC
""", nativeQuery = true)
    List<Object[]> getCourseTransactions(UUID courseId, Instant start, Instant end);

}
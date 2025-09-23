package com.pbl.elearning.commerce.repository;

import com.pbl.elearning.commerce.domain.Order;
import com.pbl.elearning.commerce.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

        Optional<Order> findByOrderNumber(String orderNumber);

        Page<Order> findByUserId(UUID userId, Pageable pageable);

        Page<Order> findByUserIdAndStatus(UUID userId, OrderStatus status, Pageable pageable);

        List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, OrderStatus status);

        @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
        Page<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId, Pageable pageable);

        @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = :status")
        Long countByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") OrderStatus status);

        // @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.userId = :userId AND
        // o.status = :status")
        // BigDecimal sumFinalAmountByUserIdAndStatus(@Param("userId") UUID userId,
        // @Param("status") OrderStatus status);

        @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.userId = :userId AND o.status = :status")
        BigDecimal sumTotalAmountByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") OrderStatus status);

        @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = :status")
        List<Order> findOrdersByDateRangeAndStatus(@Param("startDate") Timestamp startDate,
                        @Param("endDate") Timestamp endDate,
                        @Param("status") OrderStatus status);

        @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.courseId = :courseId AND o.status = :status")
        List<Order> findOrdersByCourseIdAndStatus(@Param("courseId") UUID courseId,
                        @Param("status") OrderStatus status);

        @Query("SELECT DISTINCT o FROM Order o JOIN o.items oi WHERE o.userId = :userId AND oi.courseId = :courseId AND o.status IN :statuses")
        Optional<Order> findByUserIdAndCourseIdAndStatusIn(@Param("userId") UUID userId,
                        @Param("courseId") UUID courseId,
                        @Param("statuses") List<OrderStatus> statuses);

        boolean existsByOrderNumber(String orderNumber);

        @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :cutoffTime")
        List<Order> findStaleOrdersByStatus(@Param("status") OrderStatus status,
                        @Param("cutoffTime") Timestamp cutoffTime);
}

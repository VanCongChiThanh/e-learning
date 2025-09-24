package com.pbl.elearning.commerce.repository;

import com.pbl.elearning.commerce.domain.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderId(UUID orderId);

    List<OrderItem> findByCourseId(UUID courseId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId AND oi.courseId = :courseId")
    List<OrderItem> findByUserIdAndCourseId(@Param("userId") UUID userId, @Param("courseId") UUID courseId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi JOIN oi.order o WHERE oi.courseId = :courseId AND o.status = 'DELIVERED'")
    Long countPurchasedByCourseId(@Param("courseId") UUID courseId);

    // @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi JOIN oi.order o WHERE
    // oi.courseId = :courseId AND o.status = 'DELIVERED'")
    // BigDecimal sumRevenueByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT SUM(oi.unitPrice) FROM OrderItem oi JOIN oi.order o WHERE oi.courseId = :courseId AND o.status = 'DELIVERED'")
    BigDecimal sumUnitPriceByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.userId = :userId AND o.status = 'DELIVERED'")
    Page<OrderItem> findPurchasedItemsByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT DISTINCT oi.courseId FROM OrderItem oi JOIN oi.order o WHERE o.userId = :userId AND o.status = 'DELIVERED'")
    List<UUID> findPurchasedCourseIdsByUserId(@Param("userId") UUID userId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.courseId IN :courseIds")
    List<OrderItem> findByCourseIdIn(@Param("courseIds") List<UUID> courseIds);

    boolean existsByOrderIdAndCourseId(UUID orderId, UUID courseId);
}

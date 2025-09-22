package com.pbl.elearning.commerce.repository;

import com.pbl.elearning.commerce.domain.Payment;
import com.pbl.elearning.commerce.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderCode(String orderCode);

    Optional<Payment> findByPayosPaymentLinkId(String payosPaymentLinkId);

    Optional<Payment> findByPayosTransactionId(String payosTransactionId);

    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);

    Page<Payment> findByUserId(Long userId, Pageable pageable);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Payment> findByUserIdAndStatusOrderByCreatedAtDesc(@Param("userId") Long userId,
            @Param("status") PaymentStatus status,
            Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.expiresAt < :currentTime AND p.status IN :statuses")
    List<Payment> findExpiredPayments(@Param("currentTime") Timestamp currentTime,
            @Param("statuses") List<PaymentStatus> statuses);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.userId = :userId AND p.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate AND p.status = :status")
    List<Payment> findPaymentsByDateRangeAndStatus(@Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("status") PaymentStatus status);

    boolean existsByOrderCodeAndStatus(String orderCode, PaymentStatus status);
}

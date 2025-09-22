package com.pbl.elearning.commerce.repository;

import com.pbl.elearning.commerce.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);

    @Query("SELECT COUNT(ci) FROM Cart c JOIN c.items ci WHERE c.userId = :userId")
    Integer countItemsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(c.totalAmount) FROM Cart c WHERE c.userId = :userId")
    Double getTotalAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Cart c WHERE c.totalItems > 0")
    List<Cart> findNonEmptyCarts();

    @Query("SELECT c FROM Cart c WHERE c.totalItems = 0")
    List<Cart> findEmptyCarts();

    @Query("SELECT c FROM Cart c WHERE c.updatedAt < :cutoffTime AND c.totalItems = 0")
    List<Cart> findAbandonedCarts(@Param("cutoffTime") Timestamp cutoffTime);

    @Query("SELECT c FROM Cart c WHERE c.couponCode = :couponCode")
    List<Cart> findByCouponCode(@Param("couponCode") String couponCode);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.totalItems > 0")
    Long countActiveCartsWithItems();

    void deleteByUserId(Long userId);

    @Query("SELECT c FROM Cart c WHERE c.userId IN :userIds")
    List<Cart> findByUserIdIn(@Param("userIds") List<Long> userIds);
}

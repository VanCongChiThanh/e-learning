package com.pbl.elearning.commerce.repository;

import com.pbl.elearning.commerce.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    List<CartItem> findByCourseId(Long courseId);

    Optional<CartItem> findByCartIdAndCourseId(Long cartId, Long courseId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId")
    List<CartItem> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.courseId = :courseId")
    Optional<CartItem> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.courseId = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT SUM(ci.totalPrice) FROM CartItem ci WHERE ci.cart.userId = :userId")
    BigDecimal sumTotalPriceByUserId(@Param("userId") Long userId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.coursePrice > :minPrice AND ci.coursePrice < :maxPrice")
    List<CartItem> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT ci FROM CartItem ci WHERE ci.instructorId = :instructorId")
    List<CartItem> findByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.courseCategory = :category")
    List<CartItem> findByCourseCategory(@Param("category") String category);

    @Query("SELECT ci FROM CartItem ci WHERE ci.discountPercentage > 0")
    List<CartItem> findItemsWithDiscount();

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.courseId = :courseId")
    void deleteByCartIdAndCourseId(@Param("cartId") Long cartId, @Param("courseId") Long courseId);

    @Query("SELECT DISTINCT ci.courseId FROM CartItem ci WHERE ci.cart.userId = :userId")
    List<Long> findCourseIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.courseId IN :courseIds")
    List<CartItem> findByCourseIdIn(@Param("courseIds") List<Long> courseIds);

    boolean existsByCartIdAndCourseId(Long cartId, Long courseId);

    @Query("SELECT COUNT(DISTINCT ci.cart.userId) FROM CartItem ci WHERE ci.courseId = :courseId")
    Long countDistinctUsersByCourseId(@Param("courseId") Long courseId);
}

package com.pbl.elearning.commerce.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "total_items", nullable = false)
    private Integer totalItems = 0;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    // One-to-Many relationship with CartItem
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    // Helper methods
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        recalculateAmounts();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        recalculateAmounts();
    }

    public void removeItemByCourseId(Long courseId) {
        items.removeIf(item -> item.getCourseId().equals(courseId));
        recalculateAmounts();
    }

    public void clearItems() {
        items.clear();
        recalculateAmounts();
    }

    public void recalculateAmounts() {
        this.totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        this.totalAmount = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.finalAmount = this.totalAmount.subtract(this.discountAmount);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean containsCourse(Long courseId) {
        return items.stream()
                .anyMatch(item -> item.getCourseId().equals(courseId));
    }

    public CartItem getItemByCourseId(Long courseId) {
        return items.stream()
                .filter(item -> item.getCourseId().equals(courseId))
                .findFirst()
                .orElse(null);
    }

    public void applyCoupon(String couponCode, Integer discountPercentage, BigDecimal discountAmount) {
        this.couponCode = couponCode;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        this.finalAmount = this.totalAmount.subtract(this.discountAmount);
    }

    public void removeCoupon() {
        this.couponCode = null;
        this.discountPercentage = null;
        this.discountAmount = BigDecimal.ZERO;
        this.finalAmount = this.totalAmount;
    }
}

package com.pbl.elearning.commerce.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "cart_id", "course_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends AbstractEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(name = "added_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal addedPrice;

    // Many-to-One relationship with Cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Helper methods
    // public void calculateTotalPrice() {
    // BigDecimal subtotal = coursePrice.multiply(BigDecimal.valueOf(quantity));
    // this.addedPrice = subtotal;
    // }

    // public BigDecimal getSubtotal() {
    // return coursePrice.multiply(BigDecimal.valueOf(quantity));
    // }

    // public void updateQuantity(Integer quantity) {
    // this.quantity = quantity;
    // calculateTotalPrice();
    // }

    // public void updatePrice(BigDecimal newPrice) {
    // this.coursePrice = newPrice;
    // calculateTotalPrice();
    // }

    // public boolean hasDiscount() {
    // return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) >
    // 0;
    // }

    // public BigDecimal getSavings() {
    // if (originalPrice != null && originalPrice.compareTo(coursePrice) > 0) {
    // return
    // originalPrice.subtract(coursePrice).multiply(BigDecimal.valueOf(quantity));
    // }
    // return discountAmount != null ? discountAmount : BigDecimal.ZERO;
    // }

    // Pre-persist and pre-update hooks
    // @PrePersist
    // @PreUpdate
    // public void prePersist() {
    // calculateTotalPrice();
    // }
}

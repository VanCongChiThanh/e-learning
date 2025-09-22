package com.pbl.elearning.commerce.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "course_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal coursePrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    // Many-to-One relationship with Cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Course metadata (snapshot at time of adding to cart)
    @Column(name = "course_description")
    private String courseDescription;

    @Column(name = "course_thumbnail")
    private String courseThumbnail;

    @Column(name = "instructor_name")
    private String instructorName;

    @Column(name = "course_duration_minutes")
    private Integer courseDurationMinutes;

    @Column(name = "course_level")
    private String courseLevel;

    @Column(name = "course_category")
    private String courseCategory;

    @Column(name = "instructor_id")
    private Long instructorId;

    // Price information
    @Column(name = "original_price", precision = 15, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    // Helper methods
    public void calculateTotalPrice() {
        BigDecimal subtotal = coursePrice.multiply(BigDecimal.valueOf(quantity));
        this.totalPrice = subtotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    public BigDecimal getSubtotal() {
        return coursePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    public void updatePrice(BigDecimal newPrice) {
        this.coursePrice = newPrice;
        calculateTotalPrice();
    }

    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getSavings() {
        if (originalPrice != null && originalPrice.compareTo(coursePrice) > 0) {
            return originalPrice.subtract(coursePrice).multiply(BigDecimal.valueOf(quantity));
        }
        return discountAmount != null ? discountAmount : BigDecimal.ZERO;
    }

    // Pre-persist and pre-update hooks
    @PrePersist
    @PreUpdate
    public void prePersist() {
        calculateTotalPrice();
    }
}

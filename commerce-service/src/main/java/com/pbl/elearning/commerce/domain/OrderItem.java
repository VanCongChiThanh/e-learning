package com.pbl.elearning.commerce.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends AbstractEntity {

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

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    // Many-to-One relationship with Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Course metadata (snapshot at time of purchase)
    @Column(name = "course_description")
    private String courseDescription;

    @Column(name = "course_thumbnail")
    private String courseThumbnail;

    @Column(name = "instructor_name")
    private String instructorName;

    // Helper methods
    public void calculateTotalPrice() {
        BigDecimal subtotal = coursePrice.multiply(BigDecimal.valueOf(quantity));
        this.totalPrice = subtotal.subtract(discountAmount);
    }

    public BigDecimal getSubtotal() {
        return coursePrice.multiply(BigDecimal.valueOf(quantity));
    }
}

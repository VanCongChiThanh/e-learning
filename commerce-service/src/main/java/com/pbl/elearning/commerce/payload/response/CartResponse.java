package com.pbl.elearning.commerce.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class CartResponse {

    private Long id;
    private Long userId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String couponCode;
    private Integer discountPercentage;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private List<CartItemResponse> items;

    // Cart statistics
    private BigDecimal totalSavings;
    private Integer uniqueCourses;
    private Boolean isEmpty;
    private Boolean hasCoupon;

    @Getter
    @Setter
    public static class CartItemResponse {
        private Long id;
        private Long courseId;
        private String courseName;
        private BigDecimal coursePrice;
        private Integer quantity;
        private BigDecimal totalPrice;
        private String courseDescription;
        private String courseThumbnail;
        private String instructorName;
        private Long instructorId;
        private Integer courseDurationMinutes;
        private String courseLevel;
        private String courseCategory;
        private BigDecimal originalPrice;
        private BigDecimal discountAmount;
        private Integer discountPercentage;
        private BigDecimal savings;
        private Boolean hasDiscount;
        private Timestamp addedAt;
    }
}

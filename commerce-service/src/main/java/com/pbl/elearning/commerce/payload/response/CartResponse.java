package com.pbl.elearning.commerce.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartResponse {

    private UUID id;
    private UUID userId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount; // always zero with no discounts
    private BigDecimal finalAmount; // equals totalAmount
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
        private UUID id;
        private UUID courseId;
        private String courseTitle;
        private String courseImage;
        private BigDecimal totalPrice;
        private BigDecimal discountAmount; // always zero
        private Timestamp addedAt;
    }
}

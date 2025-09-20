package com.pbl.elearning.commerce.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartSummaryResponse {

    private Long cartId;
    private Long userId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String couponCode;
    private Boolean isEmpty;
    private Boolean hasCoupon;

    // Quick stats
    private Integer uniqueCourses;
    private BigDecimal averageItemPrice;
    private BigDecimal totalSavings;
}

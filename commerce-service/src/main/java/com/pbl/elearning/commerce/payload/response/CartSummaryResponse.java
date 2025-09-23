package com.pbl.elearning.commerce.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CartSummaryResponse {

    private UUID cartId;
    private UUID userId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private Boolean isEmpty;
    private Boolean hasCoupon;

    // Quick stats
    private Integer uniqueCourses;
    private BigDecimal averageItemPrice;
    private BigDecimal totalSavings;
}

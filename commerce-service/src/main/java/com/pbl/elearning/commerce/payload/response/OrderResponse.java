package com.pbl.elearning.commerce.payload.response;

import com.pbl.elearning.commerce.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private OrderStatus status;
    private String notes;
    private String couponCode;
    private Integer discountPercentage;

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deliveredAt;

    private List<OrderItemResponse> items;
    private PaymentSummaryResponse payment;

    @Getter
    @Setter
    public static class OrderItemResponse {
        private Long id;
        private Long courseId;
        private String courseName;
        private BigDecimal coursePrice;
        private Integer quantity;
        private BigDecimal totalPrice;
        private BigDecimal discountAmount;
        private String courseDescription;
        private String courseThumbnail;
        private String instructorName;
    }

    @Getter
    @Setter
    public static class PaymentSummaryResponse {
        private Long id;
        private String orderCode;
        private String paymentMethod;
        private String status;
        private String checkoutUrl;
        private Timestamp paidAt;
        private Timestamp expiresAt;
    }
}

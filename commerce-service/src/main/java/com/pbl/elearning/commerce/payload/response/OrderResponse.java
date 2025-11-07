package com.pbl.elearning.commerce.payload.response;

import com.pbl.elearning.commerce.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderResponse {

    private UUID id;
    private String orderNumber;
    private java.util.UUID userId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private OrderStatus status;
    private String notes;

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deliveredAt;

    private List<OrderItemResponse> items;
    private PaymentSummaryResponse payment;

    @Getter
    @Setter
    public static class OrderItemResponse {
        private UUID id;
        private UUID courseId;
        private String courseTitle;
        private String courseImage;
        private BigDecimal unitPrice;
        private BigDecimal discountAmount;
    }

    @Getter
    @Setter
    public static class PaymentSummaryResponse {
        private java.util.UUID id;
        private String orderCode;
        private String paymentMethod;
        private String status;
        private String checkoutUrl;
        private Timestamp paidAt;
        private Timestamp expiresAt;
    }
}

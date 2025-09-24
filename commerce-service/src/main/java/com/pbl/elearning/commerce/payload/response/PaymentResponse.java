package com.pbl.elearning.commerce.payload.response;

import com.pbl.elearning.commerce.domain.enums.PaymentMethod;
import com.pbl.elearning.commerce.domain.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class PaymentResponse {

    private UUID id;
    private String orderCode;
    private BigDecimal amount;
    private String description;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private UUID userId;

    // PayOS specific response fields
    private String payosPaymentLinkId;
    private String payosTransactionId;
    private String checkoutUrl;
    private String qrCode;

    // Bank information
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String accountName;

    // Timestamps
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp paidAt;
    private Timestamp expiresAt;

    // Order information
    private OrderSummaryResponse order;

    @Getter
    @Setter
    public static class OrderSummaryResponse {
        private UUID id;
        private String orderNumber;
        private BigDecimal totalAmount;
        private BigDecimal finalAmount;
        private Integer totalItems;
        private String status;
    }
}

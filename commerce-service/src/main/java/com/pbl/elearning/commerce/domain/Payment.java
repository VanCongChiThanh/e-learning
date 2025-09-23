package com.pbl.elearning.commerce.domain;

import com.pbl.elearning.commerce.domain.enums.PaymentMethod;
import com.pbl.elearning.commerce.domain.enums.PaymentStatus;
import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends AbstractEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode; // Mã đơn hàng PayOS

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // PayOS specific fields
    @Column(name = "payos_payment_link_id")
    private String payosPaymentLinkId;

    @Column(name = "payos_transaction_id")
    private String payosTransactionId;

    @Column(name = "payos_reference")
    private String payosReference;

    @Column(name = "checkout_url")
    private String checkoutUrl;

    @Column(name = "qr_code")
    private String qrCode;

    // Webhook and callback data
    @Column(name = "webhook_data", columnDefinition = "TEXT")
    private String webhookData;

    @Column(name = "callback_data", columnDefinition = "TEXT")
    private String callbackData;

    // Timestamps
    @Column(name = "paid_at")
    private Timestamp paidAt;

    @Column(name = "cancelled_at")
    private Timestamp cancelledAt;

    @Column(name = "expires_at")
    private Timestamp expiresAt;

    // Bank info (from PayOS response)
    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    // One-to-One relationship with Order
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    // Helper methods
    public boolean isPaid() {
        return PaymentStatus.SUCCESS.equals(this.status);
    }

    public boolean isPending() {
        return PaymentStatus.PENDING.equals(this.status) || PaymentStatus.PROCESSING.equals(this.status);
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.before(new Timestamp(System.currentTimeMillis()));
    }
}

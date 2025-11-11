package com.pbl.elearning.commerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl.elearning.commerce.config.PayOSConfig;
import com.pbl.elearning.commerce.domain.Order;
import com.pbl.elearning.commerce.domain.Payment;
import com.pbl.elearning.commerce.domain.enums.OrderStatus;
import com.pbl.elearning.commerce.domain.enums.PaymentMethod;
import com.pbl.elearning.commerce.domain.enums.PaymentStatus;
import com.pbl.elearning.commerce.payload.request.CreatePaymentRequest;
import com.pbl.elearning.commerce.payload.response.PaymentResponse;
import com.pbl.elearning.commerce.payload.webhook.PayOSWebhookRequest;
import com.pbl.elearning.commerce.repository.OrderRepository;
import com.pbl.elearning.commerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PayOSConfig payOSConfig;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final NotificationClient notificationClient;

    private PayOS payOS;

    @PostConstruct
    public void initializePayOS() {
        this.payOS = new PayOS(
                payOSConfig.getClientId(),
                payOSConfig.getApiKey(),
                payOSConfig.getChecksumKey());
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, UUID userId) {
        // 1. Validate and get order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to this order");
        }

        if (order.isPaid()) {
            throw new RuntimeException("Order already paid");
        }

        // 2. Check if payment already exists for this order
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(order.getId());
        // Optional<Payment> existingPayment =
        // paymentRepository.findByOrderCode(order.getOrderNumber());
        if (existingPayment.isPresent() && existingPayment.get().isPending()) {
            // check expiration
            if (existingPayment.get().getExpiresAt().before(new Timestamp(System.currentTimeMillis()))) {
                existingPayment.get().setStatus(PaymentStatus.CANCELLED);
                paymentRepository.save(existingPayment.get());
            } else {
                // return existing payment if still pending
                return mapToPaymentResponse(existingPayment.get());
            }
        }

        // 3. Create new payment record
        Payment payment = createPaymentEntity(order, request, userId);
        payment = paymentRepository.save(payment);

        // 4. Create PayOS payment link
        try {
            CheckoutResponseData payosResponse = createPayOSPaymentLink(payment, order);
            updatePaymentWithPayOSResponse(payment, payosResponse);
            payment = paymentRepository.save(payment);

            log.info("Created PayOS payment for order: {}, orderCode: {}", order.getId(), payment.getOrderCode());
            return mapToPaymentResponse(payment);

        } catch (Exception e) {
            log.error("Failed to create PayOS payment for order: {}", order.getId(), e);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Failed to create payment link: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentResponse getPaymentByOrderCode(String orderCode, UUID userId) {
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to this payment");
        }

        return mapToPaymentResponse(payment);
    }

    @Transactional
    public Page<PaymentResponse> getUserPayments(UUID userId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
        return payments.map(this::mapToPaymentResponse);
    }

    @Transactional
    public boolean handlePayOSWebhook(PayOSWebhookRequest webhookRequest) {
        try {
            log.info("Received PayOS webhook for orderCode: {}", webhookRequest.getData().getOrderCode());

            // 1. Verify webhook signature
            if (!verifyWebhookSignature(webhookRequest)) {
                log.error("Invalid webhook signature for orderCode: {}", webhookRequest.getData().getOrderCode());
                return false;
            }

            // 2. Find payment by order code
            Payment payment = paymentRepository.findByOrderCode(webhookRequest.getData().getOrderCode())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // 3. Check if already processed (idempotency)
            if (payment.isPaid()) {
                log.info("Payment already processed for orderCode: {}", webhookRequest.getData().getOrderCode());
                return true;
            }

            // 4. Store webhook data
            payment.setWebhookData(objectMapper.writeValueAsString(webhookRequest));

            // 5. Process payment based on webhook status
            if (webhookRequest.isSuccess()) {
                processSuccessfulPayment(payment, webhookRequest);
            } else {
                processFailedPayment(payment, webhookRequest);
            }

            paymentRepository.save(payment);
            return true;

        } catch (Exception e) {
            log.error("Error processing PayOS webhook", e);
            return false;
        }
    }

    @Transactional
    public PaymentResponse cancelPayment(String orderCode, UUID userId) {
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to this payment");
        }

        if (!payment.isPending()) {
            throw new RuntimeException("Payment cannot be cancelled");
        }

        try {
            // Cancel payment in PayOS
            if (payment.getPayosPaymentLinkId() != null) {
                payOS.cancelPaymentLink(Long.parseLong(payment.getPayosPaymentLinkId()), "User cancelled");
            }

            // Update payment status
            payment.setStatus(PaymentStatus.CANCELLED);
            payment.setCancelledAt(new Timestamp(System.currentTimeMillis()));

            // Update order status
            Order order = payment.getOrder();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            payment = paymentRepository.save(payment);
            log.info("Cancelled payment for orderCode: {}", orderCode);

            return mapToPaymentResponse(payment);

        } catch (Exception e) {
            log.error("Failed to cancel payment for orderCode: {}", orderCode, e);
            throw new RuntimeException("Failed to cancel payment: " + e.getMessage());
        }
    }

    private Payment createPaymentEntity(Order order, CreatePaymentRequest request, UUID userId) {
        Payment payment = new Payment();
        payment.setOrderCode(generateOrderCode());
        payment.setAmount(order.getTotalAmount());
        payment.setDescription(request.getDescription() != null ? request.getDescription() : order.getOrderNumber());
        payment.setPaymentMethod(PaymentMethod.PAYOS);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserId(order.getUserId());
        payment.setOrder(order);

        // Set expiration time
        LocalDateTime expirationTime = LocalDateTime.now()
                .plusMinutes(request.getExpirationMinutes() != null ? request.getExpirationMinutes()
                        : payOSConfig.getDefaultExpirationMinutes());
        payment.setExpiresAt(Timestamp.valueOf(expirationTime));

        return payment;
    }

    private CheckoutResponseData createPayOSPaymentLink(Payment payment, Order order) throws Exception {
        // Create item data for PayOS
        ItemData itemData = ItemData.builder()
                .name(order.getOrderNumber())
                .quantity(1)
                .price(payment.getAmount().intValue())
                .build();

        // Create payment data
        PaymentData paymentData = PaymentData.builder()
                .orderCode(Long.parseLong(payment.getOrderCode()))
                .amount(payment.getAmount().intValue())
                .description(payment.getDescription())
                .items(Collections.singletonList(itemData))
                .returnUrl(payOSConfig.getReturnUrl())
                .cancelUrl(payOSConfig.getCancelUrl())
                .build();

        return payOS.createPaymentLink(paymentData);
    }

    private void updatePaymentWithPayOSResponse(Payment payment, CheckoutResponseData response) {
        payment.setPayosPaymentLinkId(response.getPaymentLinkId());
        payment.setCheckoutUrl(response.getCheckoutUrl());
        payment.setQrCode(response.getQrCode());

        if (response.getAccountNumber() != null) {
            payment.setAccountNumber(response.getAccountNumber());
        }
        if (response.getAccountName() != null) {
            payment.setAccountName(response.getAccountName());
        }
    }

    private void processSuccessfulPayment(Payment payment, PayOSWebhookRequest webhookRequest) {
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(new Timestamp(System.currentTimeMillis()));
        payment.setPayosTransactionId(webhookRequest.getData().getReference());

        // Update bank information
        payment.setBankCode(webhookRequest.getData().getCounterAccountBankId());
        payment.setBankName(webhookRequest.getData().getCounterAccountBankName());

        // Update order status
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // Grant course access (delegated to OrderService)
        orderService.grantCourseAccess(order);

        // Send payment success notification via WebSocket
        sendPaymentSuccessNotification(payment, order);

        log.info("Successfully processed payment for orderCode: {}", payment.getOrderCode());
    }

    private void processFailedPayment(Payment payment, PayOSWebhookRequest webhookRequest) {
        payment.setStatus(PaymentStatus.FAILED);

        // Update order status
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);

        // Send payment failed notification via WebSocket
        sendPaymentFailedNotification(payment, order);

        log.info("Processed failed payment for orderCode: {}", payment.getOrderCode());
    }

    private void sendPaymentSuccessNotification(Payment payment, Order order) {
        try {
            String title = "Thanh toán thành công";
            String message = String.format(
                    "Đơn hàng %s đã được thanh toán thành công. Bạn đã được cấp quyền truy cập khóa học.",
                    order.getOrderNumber());
            Map<String, String> metadata = Map.of(
                    "orderId", order.getId().toString(),
                    "orderCode", payment.getOrderCode(),
                    "orderNumber", order.getOrderNumber(),
                    "amount", payment.getAmount().toString(),
                    "paymentStatus", "SUCCESS");

            notificationClient.sendPaymentNotification(
                    payment.getUserId(),
                    "PAYMENT_SUCCESS",
                    title,
                    message,
                    payment.getOrderCode(),
                    "SUCCESS",
                    metadata);
        } catch (Exception e) {
            log.error("Failed to send payment success notification for orderCode: {}", payment.getOrderCode(), e);
        }
    }

    private void sendPaymentFailedNotification(Payment payment, Order order) {
        try {
            String title = "Thanh toán thất bại";
            String message = String.format("Thanh toán cho đơn hàng %s đã thất bại. Vui lòng thử lại.",
                    order.getOrderNumber());
            Map<String, String> metadata = Map.of(
                    "orderId", order.getId().toString(),
                    "orderCode", payment.getOrderCode(),
                    "orderNumber", order.getOrderNumber(),
                    "amount", payment.getAmount().toString(),
                    "paymentStatus", "FAILED");

            notificationClient.sendPaymentNotification(
                    payment.getUserId(),
                    "PAYMENT_FAILED",
                    title,
                    message,
                    payment.getOrderCode(),
                    "FAILED",
                    metadata);
        } catch (Exception e) {
            log.error("Failed to send payment failed notification for orderCode: {}", payment.getOrderCode(), e);
        }
    }

    private boolean verifyWebhookSignature(PayOSWebhookRequest webhookRequest) {
        // Implement PayOS webhook signature verification
        // This would use the checksum key to verify the webhook signature
        // For now, returning true - in production, implement proper verification
        return true;
    }

    private String generateOrderCode() {
        // Generate unique order code for PayOS (numeric, max 12 digits)
        long timestamp = System.currentTimeMillis() % 1000000000L; // Last 9 digits
        long random = (long) (Math.random() * 1000); // 3 random digits
        return String.format("%09d%03d", timestamp, random);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderCode(payment.getOrderCode());
        response.setAmount(payment.getAmount());
        response.setDescription(payment.getDescription());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setUserId(payment.getOrder().getUserId());
        response.setPayosPaymentLinkId(payment.getPayosPaymentLinkId());
        response.setPayosTransactionId(payment.getPayosTransactionId());
        response.setCheckoutUrl(payment.getCheckoutUrl());
        response.setQrCode(payment.getQrCode());
        response.setBankCode(payment.getBankCode());
        response.setBankName(payment.getBankName());
        response.setAccountNumber(payment.getAccountNumber());
        response.setAccountName(payment.getAccountName());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setPaidAt(payment.getPaidAt());
        response.setExpiresAt(payment.getExpiresAt());

        // Map order summary
        if (payment.getOrder() != null) {
            PaymentResponse.OrderSummaryResponse orderSummary = new PaymentResponse.OrderSummaryResponse();
            orderSummary.setId(payment.getOrder().getId());
            orderSummary.setOrderNumber(payment.getOrder().getOrderNumber());
            orderSummary.setTotalAmount(payment.getOrder().getTotalAmount());
            orderSummary.setFinalAmount(payment.getOrder().getTotalAmount());
            orderSummary.setTotalItems(payment.getOrder().getTotalItems());
            orderSummary.setStatus(payment.getOrder().getStatus().name());
            response.setOrder(orderSummary);
        }

        return response;
    }
}
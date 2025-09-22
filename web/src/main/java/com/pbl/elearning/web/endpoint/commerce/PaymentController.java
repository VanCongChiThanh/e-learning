package com.pbl.elearning.web.endpoint.commerce;

import com.pbl.elearning.commerce.payload.request.CreatePaymentRequest;
import com.pbl.elearning.commerce.payload.response.PaymentResponse;
import com.pbl.elearning.commerce.payload.webhook.PayOSWebhookRequest;
import com.pbl.elearning.commerce.service.PaymentService;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "Payment Management", description = "APIs for managing payments with PayOS")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ApiOperation(value = "Create payment", notes = "Create a new payment for an order using PayOS")
    public ResponseEntity<ResponseDataAPI> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);
        PaymentResponse paymentResponse = paymentService.createPayment(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataAPI.success(paymentResponse, "Payment created successfully"));
    }

    @GetMapping("/order-code/{orderCode}")
    @ApiOperation(value = "Get payment by order code", notes = "Get payment details by PayOS order code")
    public ResponseEntity<ResponseDataAPI> getPaymentByOrderCode(
            @PathVariable String orderCode,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);
        PaymentResponse paymentResponse = paymentService.getPaymentByOrderCode(orderCode, userId);

        return ResponseEntity.ok(
                ResponseDataAPI.success(paymentResponse, "Payment retrieved successfully"));
    }

    @GetMapping
    @ApiOperation(value = "Get user payments", notes = "Get paginated list of user payments")
    public ResponseEntity<ResponseDataAPI> getUserPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponse> payments = paymentService.getUserPayments(userId, pageable);

        return ResponseEntity.ok(
                ResponseDataAPI.success(payments, "Payments retrieved successfully"));
    }

    @DeleteMapping("/order-code/{orderCode}")
    @ApiOperation(value = "Cancel payment", notes = "Cancel a pending payment")
    public ResponseEntity<ResponseDataAPI> cancelPayment(
            @PathVariable String orderCode,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);
        PaymentResponse paymentResponse = paymentService.cancelPayment(orderCode, userId);

        return ResponseEntity.ok(
                ResponseDataAPI.success(paymentResponse, "Payment cancelled successfully"));
    }

    @PostMapping("/webhook/payos")
    @ApiOperation(value = "PayOS webhook", notes = "Handle PayOS webhook notifications")
    public ResponseEntity<String> handlePayOSWebhook(
            @RequestBody PayOSWebhookRequest webhookRequest,
            HttpServletRequest request) {

        try {
            log.info("Received PayOS webhook: {}", webhookRequest.getData().getOrderCode());

            boolean success = paymentService.handlePayOSWebhook(webhookRequest);

            if (success) {
                return ResponseEntity.ok("OK");
            } else {
                return ResponseEntity.badRequest().body("FAILED");
            }

        } catch (Exception e) {
            log.error("Error processing PayOS webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
    }

    @GetMapping("/return/payos")
    @ApiOperation(value = "PayOS return URL", notes = "Handle PayOS return from payment")
    public ResponseEntity<ResponseDataAPI> handlePayOSReturn(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String cancel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderCode) {

        try {
            log.info("PayOS return - code: {}, id: {}, cancel: {}, status: {}, orderCode: {}",
                    code, id, cancel, status, orderCode);

            if ("CANCELLED".equals(cancel)) {
                return ResponseEntity.ok(
                        ResponseDataAPI.success("Payment was cancelled by user", "Payment cancelled"));
            }

            if ("PAID".equals(status)) {
                return ResponseEntity.ok(
                        ResponseDataAPI.success("Payment completed successfully", "Payment successful"));
            }

            return ResponseEntity.ok(
                    ResponseDataAPI.success("Payment processing", "Payment status updated"));

        } catch (Exception e) {
            log.error("Error processing PayOS return", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDataAPI.error("Error processing payment return"));
        }
    }

    @GetMapping("/cancel/payos")
    @ApiOperation(value = "PayOS cancel URL", notes = "Handle PayOS cancel from payment")
    public ResponseEntity<ResponseDataAPI> handlePayOSCancel(
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String status) {

        try {
            log.info("PayOS cancel - orderCode: {}, status: {}", orderCode, status);

            return ResponseEntity.ok(
                    ResponseDataAPI.success("Payment was cancelled", "Payment cancelled"));

        } catch (Exception e) {
            log.error("Error processing PayOS cancel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDataAPI.error("Error processing payment cancellation"));
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDataAPI> hendleRuntimeException(RuntimeException ex) {
        log.error("Error in PaymentController", ex);
        return ResponseEntity.badRequest()
                .body(ResponseDataAPI.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDataAPI> handleException(Exception ex) {
        log.error("Unexpected error in PaymentController", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDataAPI.error("An unexpected error occurred"));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // TODO: Extract user ID from authentication object
        // This implementation depends on your authentication mechanism
        // For now, returning a placeholder - implement based on your security setup

        if (authentication != null && authentication.getPrincipal() != null) {
            // Example implementation - adjust based on your User details implementation
            // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            // return userPrincipal.getId();

            // Placeholder implementation
            return 1L; // Replace with actual user ID extraction
        }

        throw new RuntimeException("User not authenticated");
    }
}

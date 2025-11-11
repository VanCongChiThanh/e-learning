package com.pbl.elearning.web.endpoint.commerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl.elearning.commerce.payload.request.CreatePaymentRequest;
import com.pbl.elearning.commerce.payload.response.PaymentResponse;
import com.pbl.elearning.commerce.payload.webhook.PayOSWebhookRequest;
import com.pbl.elearning.commerce.service.PaymentService;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "Payment Management", description = "APIs for managing payments with PayOS")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ApiOperation(value = "Create payment", notes = "Create a new payment for an order using PayOS")
    public ResponseEntity<ResponseDataAPI> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @CurrentUser UserPrincipal userPrincipal) {

        PaymentResponse paymentResponse = paymentService.createPayment(request, userPrincipal.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataAPI.success(paymentResponse, "Payment created successfully"));
    }

    @GetMapping("/order-code/{orderCode}")
    @ApiOperation(value = "Get payment by order code", notes = "Get payment details by PayOS order code")
    public ResponseEntity<ResponseDataAPI> getPaymentByOrderCode(
            @PathVariable String orderCode,
            @CurrentUser UserPrincipal userPrincipal) {

        PaymentResponse paymentResponse = paymentService.getPaymentByOrderCode(orderCode, userPrincipal.getId());

        return ResponseEntity.ok(
                ResponseDataAPI.success(paymentResponse, "Payment retrieved successfully"));
    }

    @GetMapping
    @ApiOperation(value = "Get user payments", notes = "Get paginated list of user payments")
    public ResponseEntity<ResponseDataAPI> getUserPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser UserPrincipal userPrincipal) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponse> payments = paymentService.getUserPayments(userPrincipal.getId(), pageable);

        return ResponseEntity.ok(
                ResponseDataAPI.success(payments, "Payments retrieved successfully"));
    }

    @DeleteMapping("/order-code/{orderCode}")
    @ApiOperation(value = "Cancel payment", notes = "Cancel a pending payment")
    public ResponseEntity<ResponseDataAPI> cancelPayment(
            @PathVariable String orderCode,
            @CurrentUser UserPrincipal userPrincipal) {
        PaymentResponse paymentResponse = paymentService.cancelPayment(orderCode, userPrincipal.getId());

        return ResponseEntity.ok(
                ResponseDataAPI.success(paymentResponse, "Payment cancelled successfully"));
    }

    @PostMapping("/webhook/payos")
    @ApiOperation(value = "PayOS webhook", notes = "Handle PayOS webhook notifications")
    public ResponseEntity<String> handlePayOSWebhook(
            // @RequestBody PayOSWebhookRequest webhookRequest,
            @RequestBody String raw,
            HttpServletRequest request) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            PayOSWebhookRequest webhookRequest = mapper.readValue(raw, PayOSWebhookRequest.class);


            if (webhookRequest == null) {
                log.error("Webhook request is null");
                return ResponseEntity.ok("OK");
            }

            if (webhookRequest.getData() == null) {
                log.error("Webhook data is null");
                return ResponseEntity.badRequest().body("Webhook data is null");
            }

            log.info("Received PayOS webhook - Code: {}, OrderCode: {}, Desc: {}",
                    webhookRequest.getCode(),
                    webhookRequest.getData().getOrderCode(),
                    webhookRequest.getDesc());

            boolean success = paymentService.handlePayOSWebhook(webhookRequest);

            if (success) {
                return ResponseEntity.ok("OK");
            } else {
                log.warn("Webhook handled but failed internally: {}", webhookRequest);
                return ResponseEntity.ok("OK");
            }

        } catch (Exception e) {
            log.error("Error processing PayOS webhook", e);
            return ResponseEntity.ok("OK");
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
}
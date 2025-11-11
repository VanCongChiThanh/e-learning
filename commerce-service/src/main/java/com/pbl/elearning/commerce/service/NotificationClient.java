package com.pbl.elearning.commerce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class NotificationClient {

    private final WebClient webClient;

    public NotificationClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api-coursevo-dev.id.vn/api/v1/notifications")
                // .baseUrl("https://0c54e222adbf.ngrok-free.app/api/v1/notifications")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Send payment notification to user via notification-service
     */
    public void sendPaymentNotification(UUID userId, String type, String title, String message,
            String orderCode, String paymentStatus, Map<String, String> metadata) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("userId", userId.toString());
            requestBody.put("type", type);
            requestBody.put("title", title != null ? title : "Payment Notification");
            requestBody.put("message", message != null ? message : "Payment status updated");
            requestBody.put("orderCode", orderCode != null ? orderCode : "");
            requestBody.put("paymentStatus", paymentStatus != null ? paymentStatus : "");
            requestBody.put("metadata", metadata != null ? metadata : new HashMap<>());

            webClient.post()
                    .uri("/internal/payment")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("Payment notification sent successfully to user: {}", userId))
                    .doOnError(error -> log.error("Failed to send payment notification to user: {}", userId, error))
                    .subscribe();

        } catch (Exception e) {
            log.error("Error sending payment notification to user: {}", userId, e);
        }
    }
}

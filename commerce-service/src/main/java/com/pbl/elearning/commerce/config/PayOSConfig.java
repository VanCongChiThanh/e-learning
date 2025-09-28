package com.pbl.elearning.commerce.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "payos")
@Getter
@Setter
public class PayOSConfig {
    @Value("${payos.client-id}")
    private String clientId;
    @Value("${payos.api-key}")
    private String apiKey;
    @Value("${payos.checksum-key}")
    private String checksumKey;
    @Value("${payos.partner-code}")
    private String partnerCode;
    @Value("${payos.return-url}")
    private String returnUrl;
    @Value("${payos.cancel-url}")
    private String cancelUrl;
    @Value("${payos.webhook-url}")
    private String webhookUrl;
    @Value("${payos.sandbox}")
    private Boolean sandbox = true;
    @Value("${payos.default-expiration-minutes}")
    private Integer defaultExpirationMinutes;
}

package com.pbl.elearning.commerce.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payos")
@Getter
@Setter
public class PayOSConfig {

    private String clientId;
    private String apiKey;
    private String checksumKey;
    private String partnerCode;
    private String returnUrl;
    private String cancelUrl;
    private String webhookUrl;
    private Boolean sandbox = true;
    private Integer defaultExpirationMinutes = 15;
}

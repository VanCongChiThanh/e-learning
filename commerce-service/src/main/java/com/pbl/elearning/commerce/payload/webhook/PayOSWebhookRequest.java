package com.pbl.elearning.commerce.payload.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayOSWebhookRequest {

    @JsonProperty("code")
    private String code;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("data")
    private PayOSWebhookData data;

    @JsonProperty("signature")
    private String signature;

    public boolean isSuccess() {
        return "00".equals(code);
    }
}

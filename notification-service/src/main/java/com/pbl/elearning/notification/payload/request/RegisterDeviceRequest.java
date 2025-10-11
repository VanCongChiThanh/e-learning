package com.pbl.elearning.notification.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.notification.domain.enums.Platform;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class RegisterDeviceRequest {
    private UUID userId;
    @NotNull private String deviceToken;
    private Platform platform;
}
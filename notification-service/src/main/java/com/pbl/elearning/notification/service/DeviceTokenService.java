package com.pbl.elearning.notification.service;

import com.pbl.elearning.notification.domain.DeviceToken;
import com.pbl.elearning.notification.domain.enums.Platform;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenService {
    DeviceToken registerDevice(UUID userId, String deviceToken, Platform platform) ;
    List<DeviceToken> getUserDevicesByUserIds(List<UUID> userIds) ;
    void removeToken(String deviceToken) ;
}
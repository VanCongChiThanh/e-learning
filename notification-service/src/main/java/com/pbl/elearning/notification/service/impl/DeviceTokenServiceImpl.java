package com.pbl.elearning.notification.service.impl;

import com.pbl.elearning.notification.domain.DeviceToken;
import com.pbl.elearning.notification.domain.enums.Platform;
import com.pbl.elearning.notification.repository.DeviceTokenRepository;
import com.pbl.elearning.notification.service.DeviceTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public DeviceToken registerDevice(UUID userId, String deviceToken, Platform platform) {
        Optional<DeviceToken> existing = deviceTokenRepository.findByUserIdAndDeviceToken(userId, deviceToken);
        if(existing.isPresent()){
            return existing.get();
        }
        DeviceToken token = new DeviceToken();
        token.setUserId(userId);
        token.setDeviceToken(deviceToken);
        token.setPlatform(platform);
        return deviceTokenRepository.save(token);
    }

    public List<DeviceToken> getUserDevicesByUserIds(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return deviceTokenRepository.findAllByUserIdIn(userIds);
    }
    public void removeToken(String deviceToken) {
        deviceTokenRepository.deleteByDeviceToken(deviceToken);
    }
}
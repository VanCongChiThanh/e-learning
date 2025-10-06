package com.pbl.elearning.notification.service;

import com.pbl.elearning.notification.domain.DeviceToken;
import com.pbl.elearning.notification.domain.enums.Platform;
import com.pbl.elearning.notification.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

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

    public List<DeviceToken> getUserDevices(UUID userId){
        return deviceTokenRepository.findByUserId(userId);
    }
}
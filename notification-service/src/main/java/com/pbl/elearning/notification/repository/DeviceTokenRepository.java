package com.pbl.elearning.notification.repository;

import com.pbl.elearning.notification.domain.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
    List<DeviceToken> findByUserId(UUID userId);

    Optional<DeviceToken> findByUserIdAndDeviceToken(UUID userId, String deviceToken);

    List<DeviceToken> findAllByUserIdIn(List<UUID> userIds);

    void deleteByDeviceToken(String deviceToken);
}
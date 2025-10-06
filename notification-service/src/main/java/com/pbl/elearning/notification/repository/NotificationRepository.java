package com.pbl.elearning.notification.repository;

import com.pbl.elearning.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdAndIsReadFalse(UUID userId,Pageable pageable);

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    long countByUserIdAndIsReadFalse(UUID userId);
}
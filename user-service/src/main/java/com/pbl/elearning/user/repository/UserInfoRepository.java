package com.pbl.elearning.user.repository;

import com.pbl.elearning.user.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {
    Optional<UserInfo> findByUserId(UUID userId);

    List<UserInfo> findAllByUserIdIn(List<UUID> userIds);
}
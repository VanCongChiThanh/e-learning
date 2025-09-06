package com.pbl.elearning.user.service.impl;

import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.domain.enums.Gender;
import com.pbl.elearning.user.repository.UserInfoRepository;
import com.pbl.elearning.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Service
@Transactional
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository userInfoRepository;
    @Override
    public UserInfo createUserInfo(UUID userId, String firstname, String lastname, String avatar) {
        UserInfo userInfo = toUserInfoEntity(userId, firstname, lastname);
        if(avatar != null && !avatar.isEmpty()) {
            userInfo.setAvatar(avatar);
        }
        return userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo updateUserInfo(UUID userId, String firstname, String lastname, String avatar, Gender gender) {
        UserInfo userInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("UserInfo not found for userId: " + userId));
        if (firstname != null && !firstname.isEmpty()) {
            userInfo.setFirstName(firstname);
        }
        if (lastname != null && !lastname.isEmpty()) {
            userInfo.setLastName(lastname);
        }
        if (avatar != null && !avatar.isEmpty()) {
            userInfo.setAvatar(avatar);
        }
        if(gender != null){
            userInfo.setGender(gender);
        }
        return userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo getUserInfoByUserId(UUID userId) {
        return userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("UserInfo not found for userId: " + userId));
    }

    @Override
    public List<UserInfo> getAllUserInfos(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        List<UserInfo> userInfos = userInfoRepository.findAllByUserIdIn(userIds);
        if (userInfos.isEmpty()) {
            throw new NotFoundException("No UserInfo found for the provided userIds");
        }
        return userInfos;
    }

    @Override
    public List<UserInfo> getUserInfosByUserIds(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        List<UserInfo> userInfos = userInfoRepository.findAllByUserIdIn(userIds);
        if (userInfos.isEmpty()) {
            throw new NotFoundException("No UserInfo found for the provided userIds");
        }
        return userInfos;
    }

    private UserInfo toUserInfoEntity(UUID userId, String firstname, String lastname) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setFirstName(firstname);
        userInfo.setLastName(lastname);
        return userInfo;
    }
}
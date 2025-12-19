package com.pbl.elearning.user.service;


import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.domain.enums.Gender;

import java.util.List;
import java.util.UUID;

public interface UserInfoService {
    UserInfo createUserInfo(UUID userId, String firstname, String lastname, String avatar);
    UserInfo updateUserInfo(UUID userId, String firstname, String lastname, String avatar, Gender gender);
    UserInfo getUserInfoByUserId(UUID userId);
}
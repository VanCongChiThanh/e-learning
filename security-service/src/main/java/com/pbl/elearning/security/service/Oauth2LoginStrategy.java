package com.pbl.elearning.security.service;

import com.pbl.elearning.security.domain.enums.AuthProvider;
import com.pbl.elearning.security.payload.response.Oauth2Info;

public interface Oauth2LoginStrategy {
    Oauth2Info process(String accessToken);
    AuthProvider getProvider();
}
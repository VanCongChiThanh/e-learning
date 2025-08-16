package com.pbl.elearning.security.service;

import com.pbl.elearning.security.domain.enums.AuthProvider;
import com.pbl.elearning.security.payload.response.Oauth2Info;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Oauth2LoginService {
    private final List<Oauth2LoginStrategy> strategies;

    public Oauth2Info login(AuthProvider provider, String code) {
        return strategies.stream()
                .filter(s -> s.getProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Provider not supported"))
                .process(code);
    }
}
package com.pbl.elearning.security.service;

import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.domain.UserProvider;
import com.pbl.elearning.security.domain.enums.AuthProvider;

import java.util.Optional;

public interface UserProviderService {
  void create(AuthProvider provider, String providerId, String email, User user);

  Optional<UserProvider> findByProvider(String providerId, AuthProvider authProvider, String email);

  Optional<UserProvider> findByEmail(String email);

  Optional<UserProvider> findByProviderIdAndProvider(String googleId, AuthProvider authProvider);
}
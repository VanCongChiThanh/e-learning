package com.pbl.elearning.security.repository;

import com.pbl.elearning.security.domain.UserProvider;
import com.pbl.elearning.security.domain.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProviderRepository extends JpaRepository<UserProvider, UUID> {
  Optional<UserProvider> findByProviderIdAndProviderAndEmail(
          String providerId, AuthProvider authProvider, String email);

  Optional<UserProvider> findFirstByEmailOrderByCreatedAtDesc(String email);

  Optional<UserProvider> findByProviderIdAndProvider(String googleId, AuthProvider authProvider);
}
package com.pbl.elearning.security.service.impl;

import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.repository.UserRepository;
import com.pbl.elearning.security.service.UserAuthService;
import com.pbl.elearning.security.service.UserProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  @Transactional(readOnly = true)
  public User findById(UUID id) {
    Optional<User> user = userRepository.findById(id);
    return user.orElseThrow(() -> new NotFoundException(MessageConstant.USER_NOT_FOUND));
  }
}
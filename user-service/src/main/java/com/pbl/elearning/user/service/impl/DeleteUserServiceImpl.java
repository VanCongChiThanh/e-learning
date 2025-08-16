package com.pbl.elearning.user.service.impl;


import com.pbl.elearning.user.service.DeleteUserService;
import com.pbl.elearning.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteUserServiceImpl implements DeleteUserService {
  private final UserService userService;

  @Override
  public void delete(UUID id, String token) {
    userService.removeAccount(id, token);
  }
}
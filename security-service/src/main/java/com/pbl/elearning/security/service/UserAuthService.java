package com.pbl.elearning.security.service;



import com.pbl.elearning.security.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthService {

  /**
   * Get user by email
   *
   * @param email User's email
   * @return Optional {@link User}
   */
  Optional<User> findByEmail(String email);

  /**
   * Get User by id
   *
   * @param id User's id
   * @return User
   */
  User findById(UUID id);


}
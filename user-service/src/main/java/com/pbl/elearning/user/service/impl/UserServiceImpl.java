package com.pbl.elearning.user.service.impl;


import com.pbl.elearning.common.common.CommonFunction;
import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.domain.enums.Role;
import com.pbl.elearning.common.exception.*;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.email.service.EmailService;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.domain.enums.ActiveStatus;
import com.pbl.elearning.security.domain.enums.AuthProvider;
import com.pbl.elearning.security.repository.UserRepository;
import com.pbl.elearning.security.service.UserProviderService;
import com.pbl.elearning.user.config.DomainProperties;
import com.pbl.elearning.user.payload.request.user.ChangePasswordRequest;
import com.pbl.elearning.user.payload.request.user.ForgotPasswordRequest;
import com.pbl.elearning.user.payload.request.user.ResetPasswordRequest;
import com.pbl.elearning.user.service.UserInfoService;
import com.pbl.elearning.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@EnableConfigurationProperties(DomainProperties.class)
public class UserServiceImpl implements UserService {
  private static final String EMAIL_PREFIX = "@";

  private static final String LANGUAGE_CODE = "vi";

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final EmailService emailService;

  private final UserProviderService userProviderService;

  private final UserInfoService userInfoService;

  private final DomainProperties domainProperties;

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  @Transactional(readOnly = true)
  public User findById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(MessageConstant.USER_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public Set<User> findByIds(Set<UUID> ids) {
    return userRepository.findAllByIdIn(ids);
  }

  @Override
  public List<UUID> findAllByRole(Role role) {
    List<User> users = userRepository.findAllByRole(role);
    List<UUID> uuids = new ArrayList<>();
    for (User user : users) {
      uuids.add(user.getId());
    }
    return uuids;
  }

  @Override
  public ResponseDataAPI changePassword(
      UUID userId, String oldPassword, ChangePasswordRequest changePasswordRequest) {
    try {
      if (!BCrypt.checkpw(changePasswordRequest.getOldPassword(), oldPassword)) {
        throw new BadRequestException(MessageConstant.CHANGE_CREDENTIAL_NOT_CORRECT);
      }

      if (!changePasswordRequest
          .getConfirmNewPassword()
          .equals(changePasswordRequest.getNewPassword())) {
        throw new BadRequestException(MessageConstant.PASSWORD_FIELDS_MUST_MATCH);
      }

      if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
        throw new BadRequestException(MessageConstant.NEW_PASSWORD_NOT_SAME_OLD_PASSWORD);
      }

      User user = findById(userId);
      user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

      userRepository.save(user);
      return ResponseDataAPI.success(null, null);
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new InternalServerException(MessageConstant.CHANGE_CREDENTIAL_FAIL);
    }
  }

  @Override
  public User registerUser(
      String firstname, String lastname, String email, String password, Role role) {
    if(role.equals(Role.ROLE_ADMIN)) {
      throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
    }
    if (userRepository.existsByEmail(email.toLowerCase())) {
      throw new BadRequestException(MessageConstant.REGISTER_EMAIL_ALREADY_IN_USE);
    }
    User user = this.toUserEntity(email, password);
    user.setRole(role);
    User result = userRepository.save(user);
    userProviderService.create(AuthProvider.LOCAL, null, email.toLowerCase(), result);
    userInfoService.createUserInfo(result.getId(), firstname, lastname,null);
    emailService.sendMailConfirmRegister(
        firstname, lastname, result.getEmail(), result.getConfirmationToken(), LANGUAGE_CODE);
    return result;
  }
  @Override
  public User registerUserOauth2(
      String firstname, String lastname, String email, String avatar, AuthProvider provider,String providerId) {
    Optional<User> existingUser = userRepository.findByEmail(email.toLowerCase());
    if (existingUser.isPresent()) {
      User user = existingUser.get();
      userInfoService.updateUserInfo(user.getId(), firstname, lastname, avatar);
      return user;
    }
    User user = this.toUserEntity(email, null);
    user.setRole(Role.ROLE_LEARNER);
    user.setIsEnabled(true);
    User result = userRepository.save(user);
    userProviderService.create(provider, providerId, email.toLowerCase(), result);
    userInfoService.createUserInfo(result.getId(), firstname, lastname,avatar);
    return result;
  }
  private User toUserEntity(String email, String password) {
    User user = new User();
    user.setEmail(email.toLowerCase());
    if (password != null) {
      user.setPassword(passwordEncoder.encode(password));
    }
    user.setConfirmationToken(UUID.randomUUID());
    user.setActiveStatus(ActiveStatus.ACCEPT);
    return user;
  }

  @Override
  public ResponseEntity<ResponseDataAPI> confirmEmailRegister(String confirmationToken) {
    User user =
        userRepository
            .findByConfirmationToken(UUID.fromString(confirmationToken))
            .orElseThrow(
                () -> new ForbiddenException(MessageConstant.INCORRECT_CONFIRMATION_TOKEN));
    if (user.getConfirmedAt() != null) {
      throw new ForbiddenException(MessageConstant.USER_IS_ENABLED);
    }
    user.setIsEnabled(true);
    user.setConfirmedAt(CommonFunction.getCurrentDateTime());
    userRepository.save(user);

    return ResponseEntity.ok(
        ResponseDataAPI.success(user.getRole().toString().replace("ROLE_", ""), null));
  }

  @Override
  public ResponseDataAPI forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    User user =
        userRepository
            .findByEmail(forgotPasswordRequest.getEmail().toLowerCase())
            .orElseThrow(() -> new NotFoundException(MessageConstant.USER_NOT_FOUND));

    user.setResetPasswordToken(UUID.randomUUID());
    user.setResetPasswordSentAt(CommonFunction.getCurrentDateTime());
    User result = userRepository.save(user);
    emailService.sendMailForgetPassword(
        result.getEmail(), result.getResetPasswordToken(), LANGUAGE_CODE, user.getRole());
    return ResponseDataAPI.success(null, null);
  }

  @Override
  public ResponseDataAPI resetPassword(ResetPasswordRequest resetPasswordRequest) {
    if (!resetPasswordRequest
        .getPassword()
        .equals(resetPasswordRequest.getPasswordConfirmation())) {
      throw new BadRequestException(MessageConstant.PASSWORD_FIELDS_MUST_MATCH);
    }
    User user =
        userRepository
            .findByEmail(resetPasswordRequest.getEmail())
            .orElseThrow(() -> new NotFoundException(MessageConstant.USER_NOT_FOUND));
    if (user.getResetPasswordToken() == null
        || !user.getResetPasswordToken().equals(resetPasswordRequest.getResetPasswordToken())) {
      throw new UnauthorizedException(MessageConstant.RESET_CREDENTIAL_FAIL);
    }
    user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
    user.setResetPasswordToken(null);
    userRepository.save(user);
    return ResponseDataAPI.success(null, null);
  }

  @Override
  public void setActiveUser(User user) {
    user.setIsEnabled(true);
    user.setConfirmedAt(CommonFunction.getCurrentDateTime());
    userRepository.save(user);
  }

  @Override
  public void deactivateUser(String reason, User user) {
    user.setIsEnabled(false);
    userRepository.save(user);
    userRepository.revokeAll(user.getId());
    //    emailService.sendMailDeactivateUserByAdmin(reason, user);
  }

  @Override
  public void resendMailActiveRequest(User user, String firstname, String lastname) {

    user.setConfirmationToken(UUID.randomUUID());

    if (user.getIsEnabled() && user.getConfirmedAt() != null) {
      throw new ForbiddenException(MessageConstant.USER_IS_ENABLED);
    }
    User result = userRepository.save(user);
    emailService.sendMailConfirmRegister(
        firstname, lastname, result.getEmail(), result.getConfirmationToken(), LANGUAGE_CODE);
  }

  @Override
  public void requestRemoveAccount(UUID userId) {
    User user = this.findById(userId);
    user.setRequestDeleteAt(CommonFunction.getCurrentDateTime());
    user.setRequestDeleteCode(CommonFunction.generateCodeDigit(6));
    emailService.sendMailDeleteAccount(user.getEmail(), user.getRequestDeleteCode(), LANGUAGE_CODE);
  }

  @Override
  public void removeAccount(UUID userId, String token) {
    User user = this.findById(userId);
    if (user.getRequestDeleteAt() == null) {
      throw new ForbiddenException(MessageConstant.TOKEN_REMOVE_ACCOUNT_NOT_CORRECT);
    }

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(user.getRequestDeleteAt().getTime());
    cal.add(Calendar.SECOND, 300);

    if (CommonFunction.getCurrentDateTime().compareTo(new Timestamp(cal.getTime().getTime())) > 0) {
      throw new ForbiddenException(MessageConstant.TOKEN_REMOVE_ACCOUNT_EXPIRED);
    }

    if (!token.equals(user.getRequestDeleteCode())) {
      throw new ForbiddenException(MessageConstant.TOKEN_REMOVE_ACCOUNT_NOT_CORRECT);
    }
    user.setEmailRestore(user.getEmail());
    user.setEmail(user.getId() + EMAIL_PREFIX + domainProperties.getDomain());
    user.setIsEnabled(false);
    userRepository.save(user);
  }

  @Override
  public User registerUser(
      UUID userId, String firstname, String lastname, String email, String password, Role role) {
    if (userRepository.existsByEmail(email.toLowerCase())) {
      throw new BadRequestException(MessageConstant.REGISTER_EMAIL_ALREADY_IN_USE);
    }
    User user = this.toUserEntity(email, password);
    user.setId(userId);
    user.setRole(role);
    User result = userRepository.save(user);
    userProviderService.create(AuthProvider.LOCAL, null, email.toLowerCase(), result);

    emailService.sendMailConfirmRegister(
        firstname, lastname, result.getEmail(), result.getConfirmationToken(), LANGUAGE_CODE);
    return result;
  }
}
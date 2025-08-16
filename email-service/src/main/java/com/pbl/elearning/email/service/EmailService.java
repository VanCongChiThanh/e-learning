package com.pbl.elearning.email.service;




import com.pbl.elearning.common.domain.enums.Role;

import java.util.UUID;

public interface EmailService {
  /**
   * Send mail confirm register
   *
   * @param firstname Firstname
   * @param lastname Lastname
   * @param email Email
   * @param confirmToken Confirm token
   * @param language Language's code
   */
  void sendMailConfirmRegister(
      String firstname, String lastname, String email, UUID confirmToken, String language);

  /**
   * Send mail forget password
   *
   * @param email Email
   * @param resetPasswordToken Reset password token
   * @param language Language's code
   * @param role {@link Role}
   */
  void sendMailForgetPassword(String email, UUID resetPasswordToken, String language, Role role);

  /**
   * Send mail delete account
   *
   * @param email Email
   * @param code Code
   * @param language Language's code
   */
  void sendMailDeleteAccount(String email, String code, String language);
}
package com.pbl.elearning.email.service.impl;


import com.pbl.elearning.common.domain.enums.Role;
import com.pbl.elearning.email.service.EmailService;
import com.pbl.elearning.email.service.SendEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final SendEmailService sendEmailService;

  @Value("${web-url}")
  private String webURL;

  @Override
  public void sendMailConfirmRegister(
      String firstname, String lastname, String email, UUID confirmToken, String language) {
    String url = webURL + "/register/confirm?token=" + confirmToken;

    sendEmailService.sendEmailFromTemplate(
        email,
            "mail/activationUserMail",
        url,
        "email.register.user.subject",
        firstname.concat(" ").concat(lastname),
        language);
  }

  @Override
  public void sendMailForgetPassword(
      String email, UUID resetPasswordToken, String language, Role role) {
    String url = webURL + "/password/reset?email=" + email + "&token=" + resetPasswordToken;
    sendEmailService.sendEmailFromTemplate(
        email, "mail/resetPassword", url, "email.reset.password.subject", null, language);
  }

  @Override
  public void sendMailDeleteAccount(String email, String code, String language) {
    sendEmailService.sendDeleteAccount(email, code, language);
  }
}
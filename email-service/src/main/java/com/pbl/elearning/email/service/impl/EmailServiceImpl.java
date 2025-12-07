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

  @Override
  public void sendMailConfirmInstructorApplication(String firstname, String lastname, String email, UUID applicationId, String language) {
    String url = webURL + "/instructor/application/confirm?applicationId=" + applicationId;

    sendEmailService.sendEmailFromTemplate(
        email,
        "mail/confirmInstructorApplication",
        url,
        "email.confirm.instructor.application.subject",
        firstname.concat(" ").concat(lastname),
        language);
  }

  @Override
  public void sendMailRejectInstructorApplication(String firstname, String lastname, String email, UUID applicationId, String reason, String language) {
    String url = webURL + "/instructor/application/reject?applicationId=" + applicationId;

    sendEmailService.sendEmailFromTemplate(
        email,
        "mail/rejectInstructorApplication",
        url,
        "email.reject.instructor.application.subject",
        firstname.concat(" ").concat(lastname),
        language,
        reason);
  }

  @Override
  public void sendMailConfirmBankAccount(String bankName, String accountNumber, String accountHolderName, String token,String email, String language) {
    String url = webURL + "/bank/account/confirm?token=" + token;
    String bankInfo = String.format(
            "Ngân hàng: %s\nSTK: %s\nChủ tài khoản: %s\nMã xác thực: %s",
            bankName, accountNumber, accountHolderName, token
    );
    sendEmailService.sendEmailFromTemplate(
        email,
        "mail/confirmBankAccount",
        url,
        "email.confirm.bank.account.subject",
        bankInfo,
        language);
  }
}
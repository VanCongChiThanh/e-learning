package com.pbl.elearning.email.service.impl;


import com.pbl.elearning.email.service.SendEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEmailImpl implements SendEmailService {

  private final MessageSource messageSource;

  private final SpringTemplateEngine springTemplateEngine;

  private final JavaMailSender emailSender;

  @Override
  @Async("asyncExecutor")
  public void sendEmailFromTemplate(
      String email,
      String templateName,
      String referUrl,
      String titleKey,
      String additionText,
      String language) {
    Context context = new Context();
    Locale locale = new Locale(language);

    context.setLocale(locale);
    if (Objects.nonNull(referUrl)) {
      context.setVariable("url", referUrl);
    }
    context.setVariable("additionText", additionText);
    String subject = messageSource.getMessage(titleKey, null, locale);
    String detail = springTemplateEngine.process(templateName, context);

    this.sendMail(email, subject, detail);
  }

  @Override
  @Async("asyncExecutor")
  public void sendEmailFromTemplate(
      String email,
      String templateName,
      String referUrl,
      String titleKey,
      String additionText,
      String language,
      String reason) {
    Context context = new Context();
    Locale locale = new Locale(language);

    context.setLocale(locale);
    if (Objects.nonNull(referUrl)) {
      context.setVariable("url", referUrl);
    }
    context.setVariable("additionText", additionText);
    context.setVariable("email", email);
    context.setVariable("reason", reason);
    String subject = messageSource.getMessage(titleKey, null, locale);
    String detail = springTemplateEngine.process(templateName, context);
    this.sendMail(email, subject, detail);
  }

  @Override
  @Async("asyncExecutor")
  public void sendEmailFromTemplate(
      String email,
      String templateName,
      String referUrl,
      String titleKey,
      String additionText,
      String reason,
      String language,
      List<String> fileNames) {

    Context context = new Context();
    Locale locale = new Locale(language);

    context.setLocale(locale);
    if (Objects.nonNull(referUrl)) {
      context.setVariable("url", referUrl);
    }
    //    context.setVariable("user", receiver);
    context.setVariable("additionText", additionText);
    if (!reason.isEmpty()) {
      context.setVariable("reason", reason);
    }
    String subject = messageSource.getMessage(titleKey, null, locale);
    String detail = springTemplateEngine.process(templateName, context);
    this.sendMail(email, subject, detail);
  }

  @Override
  @Async("asyncExecutor")
  public void sendDeleteAccount(String email, String code, String language) {
    Context context = new Context();
    Locale locale = new Locale(language);

    context.setLocale(locale);

    context.setVariable("code", code);
    String subject = messageSource.getMessage("email.delete.user.subject", null, locale);
    String detail = springTemplateEngine.process("mail/deleteUserMail", context);
    this.sendMail(email, subject, detail);
  }

  private void sendMail(String email, String subject, String detail) {
    try {
      MimeMessage message = emailSender.createMimeMessage();

      MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");
      messageHelper.setFrom("vancongchithanh2004@gmail.com");
      messageHelper.setTo(email);
      messageHelper.setSubject(subject);
      messageHelper.setText(detail, true);
      emailSender.send(message);
    } catch (Exception e) {
      log.error("Email error");
    }
  }
}
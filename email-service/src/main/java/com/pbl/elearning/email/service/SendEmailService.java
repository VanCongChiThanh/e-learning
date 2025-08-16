package com.pbl.elearning.email.service;

import java.util.List;

public interface SendEmailService {

  /**
   * Send mail with template
   *
   * @param email Email
   * @param templateName Template's name used
   * @param referUrl Reference url
   * @param titleKey Title key
   * @param additionText Addition information
   * @param language Language's code
   */
  void sendEmailFromTemplate(
      String email,
      String templateName,
      String referUrl,
      String titleKey,
      String additionText,
      String language);

  /**
   * Send mail with template
   *
   * @param email Email
   * @param templateName Template's name used
   * @param referUrl Reference url
   * @param titleKey Title key
   * @param additionText Addition information
   * @param language Language's code
   * @param reason Reason
   */
  void sendEmailFromTemplate(
      String email,
      String templateName,
      String referUrl,
      String titleKey,
      String additionText,
      String language,
      String reason);

  /**
   * Send mail with template
   *
   * @param email Email
   * @param templateName Template's name used
   * @param referUrl Reference url
   * @param titleKey Title key
   * @param additionText Addition information
   * @param reason Reason
   * @param language Language's code
   * @param attachments File attachments
   */
  void sendEmailFromTemplate(
      String email,
      String templateName,
      String referUrl,
      String titleKey,
      String additionText,
      String reason,
      String language,
      List<String> attachments);

  void sendDeleteAccount(String email, String code, String language);
}
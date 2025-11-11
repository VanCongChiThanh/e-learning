package com.pbl.elearning.common.constant;

public final class MessageConstant {

  public static final String USER_NOT_FOUND = "user_not_found";

  // Page not found
  public static final String PAGE_NOT_FOUND = "page_not_found";
  // Forbidden error
  public static final String FORBIDDEN_ERROR = "forbidden_error";
  // Unauthorized
  public static final String UNAUTHORIZED = "unauthorized";

  // Internal server error
  public static final String INTERNAL_SERVER_ERROR = "internal_server_error";

  // User
  public static final String CHANGE_CREDENTIAL_FAIL = "change_password_failed";
  public static final String RESET_CREDENTIAL_FAIL = "reset_password_failed";
  public static final String INCORRECT_EMAIL_OR_PASSWORD = "incorrect_email_or_password";
  public static final String ACCOUNT_NOT_EXISTS = "account_not_exists";
  public static final String ACCOUNT_BLOCKED = "account_blocked";
  public static final String ACCOUNT_NOT_ACTIVATED = "account_not_activated";
  public static final String USER_IS_ENABLED = "user_is_enabled";
  public static final String INCORRECT_CONFIRMATION_TOKEN = "incorrect_confirmation_token";
  public static final String CHANGE_CREDENTIAL_NOT_CORRECT = "old_password_not_correct";
  public static final String REGISTER_EMAIL_ALREADY_IN_USE = "email_already_in_use";
  public static final String PASSWORD_FIELDS_MUST_MATCH = "password_fields_must_match";
  public static final String NEW_PASSWORD_NOT_SAME_OLD_PASSWORD =
      "new_password_not_same_old_password";
  public static final String TOKEN_REMOVE_ACCOUNT_NOT_CORRECT = "token_remove_account_not_correct";
  public static final String TOKEN_REMOVE_ACCOUNT_EXPIRED = "token_remove_account_expired";
  public static final String MAXIMUM_UPLOAD_SIZE_EXCEEDED = "maximum_upload_size_exceeded";

  // Authentication
  public static final String INVALID_TOKEN = "invalid_token";
  public static final String EXPIRED_TOKEN = "expired_token";
  public static final String REVOKED_TOKEN = "revoked_token";
  public static final String INVALID_REFRESH_TOKEN = "invalid_refresh_token";
  public static final String EXPIRED_REFRESH_TOKEN = "expired_refresh_token";

  public static final String USER_INFO_ALREADY_EXISTS = "user_info_already_exists";

  public static final String USER_INFO_NOT_FOUND = "user_info_not_found";

  public static final String DYNAMIC_LINK_PROPERTY_NOT_FOUND = "dynamic_link_property_not_found";
  public static final String NOTE_NOT_FOUND = "note_not_found";
  public static final String LABEL_NOT_FOUND = "label_not_found";
  public static final String LABEL_NOT_ATTACHED_TO_NOTE = "label_not_attached_to_note";
  public static final String LABEL_ALREADY_ATTACHED_TO_NOTE = "label_already_attached_to_note";
  public static final String REMINDER_ALREADY_SET_TO_NOTE = "reminder_already_set_to_note";
  public static final String NOTIFICATION_NOT_FOUND = "notification_not_found";

  public static final String FILE_NOT_FORMAT = "file_not_format";
  public static final String FILE_NOT_FOUND = "file_not_found";
  public static final String FILE_URL_IS_ERROR = "file_url_is_error";
  public static final String FILE_IS_DELETED_FAILED = "file_is_deleted_failed";

  public static final String INSTRUCTOR_PROFILE_NOT_FOUND="instructor_profile_not_found";

  public static final String ALREADY_INSTRUCTOR="already_instructor_role";
  public static final String USER_NOT_VERIFIED="user_not_verified";
  public static final String TOO_MANY_REQUEST = "too_many_request";
  public static final String APPLICATION_ALREADY_PENDING = "application_already_pending";
  public static final String APPLICATION_ALREADY_APPROVED = "application_already_approved";
  public static final String MOTIVATION_TOO_SHORT = "motivation_too_short";
  public static final String APPLICATION_COOLDOWN = "application_cooldown";
  public static final String INVALID_STATUS_REVIEW_APPLICATION = "invalid_status_for_review_application";
  public static final String APPLICATION_NOT_FOUND = "application_not_found";
  private MessageConstant() {}
}
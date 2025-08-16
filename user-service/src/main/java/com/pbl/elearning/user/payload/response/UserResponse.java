package com.pbl.elearning.user.payload.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.domain.enums.ActiveStatus;
import com.pbl.elearning.security.domain.enums.AuthProvider;
import com.pbl.elearning.user.domain.UserInfo;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String avatar;

    private String role;

    @Enumerated(EnumType.STRING)
    private ActiveStatus activeStatus;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private boolean active;

    private Timestamp confirmedAt;
    private Timestamp createdAt;
    private Timestamp deletedAt;

  /**
   * Create user response with main information
   *
   * @param user {@link com.pbl.elearning.security.domain.User}
   * @return UserResponse
   */
  public static UserResponse createUserResponseWithMainInfo(User user, UserInfo info, AuthProvider provider) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .role(user.getSubRole())
        .firstName(info != null ? info.getFirstName() : null)
        .lastName(info != null ? info.getLastName() : null)
        .avatar(info != null ? info.getAvatar() : null)
        .provider(provider)
        .active(user.getIsEnabled())
        .activeStatus(user.getActiveStatus())
        .build();
  }
}
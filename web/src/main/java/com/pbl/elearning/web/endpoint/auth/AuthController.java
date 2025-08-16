package com.pbl.elearning.web.endpoint.auth;


import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.domain.enums.Role;
import com.pbl.elearning.common.exception.BadRequestException;
import com.pbl.elearning.common.exception.ForbiddenException;
import com.pbl.elearning.common.exception.UnauthorizedException;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.security.domain.enums.AuthProvider;
import com.pbl.elearning.security.payload.request.LoginRequest;
import com.pbl.elearning.security.payload.request.RefreshTokenRequest;
import com.pbl.elearning.security.payload.response.Oauth2Info;
import com.pbl.elearning.security.service.Oauth2LoginService;
import com.pbl.elearning.security.service.OauthAccessTokenService;
import com.pbl.elearning.security.token.TokenProvider;
import com.pbl.elearning.user.payload.request.user.SignOutAllRequest;
import com.pbl.elearning.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Api(tags = "Auth APIs")
public class AuthController {

  private final AuthenticationManager authenticationManager;

  private final TokenProvider tokenProvider;

  private final OauthAccessTokenService oauthAccessTokenService;

  private final UserService userService;
  private final Oauth2LoginService oauth2LoginService;

  /**
   * Login
   *
   * @param loginRequest {@link LoginRequest}
   * @return ResponseDataAPI
   */
  @PostMapping("/oauth/token")
  public ResponseEntity<ResponseDataAPI> login(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getEmail().toLowerCase(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      if (userPrincipal.getRole().equals(Role.ROLE_ADMIN)) {
        throw new UnauthorizedException(MessageConstant.FORBIDDEN_ERROR);
      }

      return ResponseEntity.ok(
          ResponseDataAPI.success(
              tokenProvider.createOauthAccessToken(userPrincipal, AuthProvider.LOCAL), null));
    } catch (BadCredentialsException e) {
      throw new BadRequestException(MessageConstant.INCORRECT_EMAIL_OR_PASSWORD);
    } catch (InternalAuthenticationServiceException e) {
      throw new UnauthorizedException(MessageConstant.ACCOUNT_NOT_EXISTS);
    } catch (DisabledException e) {
      Optional<User> optional = userService.findByEmail(loginRequest.getEmail());
      if (optional.isPresent() && optional.get().getConfirmedAt() != null) {
        throw new UnauthorizedException(MessageConstant.ACCOUNT_BLOCKED);
      } else {
        throw new UnauthorizedException(MessageConstant.ACCOUNT_NOT_ACTIVATED);
      }
    } catch (AuthenticationException e) {
      throw new UnauthorizedException(MessageConstant.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Login
   *
   * @param loginRequest {@link LoginRequest}
   * @return ResponseDataAPI
   */
  @PostMapping("/admin/oauth/token")
  public ResponseEntity<ResponseDataAPI> loginAdmin(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getEmail().toLowerCase(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      if (!userPrincipal.getRole().equals(Role.ROLE_ADMIN)) {
        throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
      }
      return ResponseEntity.ok(
          ResponseDataAPI.success(
              tokenProvider.createOauthAccessToken(userPrincipal, AuthProvider.LOCAL), null));
    } catch (BadCredentialsException e) {
      throw new BadRequestException(MessageConstant.INCORRECT_EMAIL_OR_PASSWORD);
    } catch (InternalAuthenticationServiceException e) {
      throw new UnauthorizedException(MessageConstant.ACCOUNT_NOT_EXISTS);
    } catch (DisabledException e) {
      Optional<User> optional = userService.findByEmail(loginRequest.getEmail());
      if (optional.isPresent() && optional.get().getConfirmedAt() != null) {
        throw new UnauthorizedException(MessageConstant.ACCOUNT_BLOCKED);
      } else {
        throw new UnauthorizedException(MessageConstant.ACCOUNT_NOT_ACTIVATED);
      }
    } catch (AuthenticationException e) {
      throw new UnauthorizedException(MessageConstant.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("oauth2/{provider}/login")
  public ResponseEntity<ResponseDataAPI> oauth2Login(
          @PathVariable("provider") String provider,
          @RequestParam("code") String code) {
    log.info("OAuth2 login with provider: {}, code: {}", provider, code);
    AuthProvider authProvider= AuthProvider.valueOf(provider.toUpperCase());
    Oauth2Info info = oauth2LoginService.login(authProvider, code);
    User user=userService.registerUserOauth2(
            info.getFirstName(),info.getLastName(), info.getEmail(), info.getAvatarUrl(), authProvider, info.getProviderId());
    return ResponseEntity.ok(
        ResponseDataAPI.success(
            tokenProvider.createOauthAccessToken(
                UserPrincipal.create(user), authProvider),null));
  }
  /**
   * Refresh token
   *
   * @param refreshTokenRequest {@link RefreshTokenRequest}
   * @return ResponseDataAPI
   */
  @PostMapping("/refresh_tokens")
  public ResponseEntity<ResponseDataAPI> refreshTokenUser(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    return ResponseEntity.ok(
        ResponseDataAPI.success(
            tokenProvider.refreshTokenOauthAccessToken(
                refreshTokenRequest.getRefreshToken(), false),
            null));
  }

  /**
   * Refresh token
   *
   * @param refreshTokenRequest {@link RefreshTokenRequest}
   * @return ResponseDataAPI
   */
  @PostMapping("/admin/refresh_tokens")
  public ResponseEntity<ResponseDataAPI> refreshTokenAdmin(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    return ResponseEntity.ok(
        ResponseDataAPI.success(
            tokenProvider.refreshTokenOauthAccessToken(refreshTokenRequest.getRefreshToken(), true),
            null));
  }

  /**
   * Revoke token
   *
   * @param request {@link HttpServletRequest}
   * @return ResponseDataAPI
   */
  @PostMapping("/oauth/revoke")
  public ResponseEntity<ResponseDataAPI> logout(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      bearerToken = bearerToken.substring(7);
    }
    oauthAccessTokenService.revoke(tokenProvider.getOauthAccessTokenFromToken(bearerToken));
    return ResponseEntity.ok(ResponseDataAPI.success(null, null));
  }

  /**
   * Revoke all token
   *
   * @param signOutAllRequest {@link SignOutAllRequest}
   * @return ResponseDataAPI
   */
  @PostMapping("/oauth/revoke-all")
  public ResponseEntity<ResponseDataAPI> allLogout(
          @Valid @RequestBody SignOutAllRequest signOutAllRequest, HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (!StringUtils.hasText(bearerToken)) {
      oauthAccessTokenService.revokeAll(null, signOutAllRequest.getEmail());
      return ResponseEntity.ok(ResponseDataAPI.success(null, null));
    } else {
      if (bearerToken.startsWith("Bearer ")) {
        bearerToken = bearerToken.substring(7);
      }
      oauthAccessTokenService.revokeAll(
          tokenProvider.getOauthAccessTokenFromToken(bearerToken), signOutAllRequest.getEmail());
    }
    return ResponseEntity.ok(ResponseDataAPI.success(null, null));
  }
}
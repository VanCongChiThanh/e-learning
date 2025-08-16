package com.pbl.elearning.security.token;



import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.domain.enums.Role;
import com.pbl.elearning.common.exception.ForbiddenException;
import com.pbl.elearning.security.config.AppProperties;
import com.pbl.elearning.security.domain.OauthAccessToken;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.security.domain.enums.AuthProvider;
import com.pbl.elearning.security.mapper.OauthAccessTokenMapper;
import com.pbl.elearning.security.payload.response.OauthAccessTokenResponse;
import com.pbl.elearning.security.service.OauthAccessTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private final AppProperties appProperties;

  private final OauthAccessTokenService oauthAccessTokenService;

  private final OauthAccessTokenMapper oauthAccessTokenMapper;

  public OauthAccessTokenResponse createOauthAccessToken(
          UserPrincipal userPrincipal, AuthProvider provider) {
    return this.createToken(userPrincipal.getId(), provider);
  }

  public OauthAccessTokenResponse createToken(UUID userId, AuthProvider provider) {
    OauthAccessToken oauthAccessToken = oauthAccessTokenService.createToken(userId, null, provider);
    return oauthAccessTokenMapper.toOauthAccessTokenResponse(
        oauthAccessToken,
        this.createAccessToken(oauthAccessToken),
        this.createRefreshToken(oauthAccessToken),
        appProperties.getAuth().getTokenExpirationMsec() / 1000);
  }

  public OauthAccessTokenResponse refreshTokenOauthAccessToken(
      String refreshToken, boolean isAdmin) {
    this.validateRefreshToken(refreshToken, appProperties.getAuth().getRefreshTokenSecret());
    UUID refreshTokenId =
        this.getUUIDFromToken(refreshToken, appProperties.getAuth().getRefreshTokenSecret());

    OauthAccessToken oauthAccessToken =
        oauthAccessTokenService.getOauthAccessTokenByRefreshToken(refreshTokenId);
    if (isAdmin && !oauthAccessToken.getUser().getRole().equals(Role.ROLE_ADMIN)
        || !isAdmin && oauthAccessToken.getUser().getRole().equals(Role.ROLE_ADMIN)) {
      throw new ForbiddenException(MessageConstant.FORBIDDEN_ERROR);
    }
    OauthAccessToken result =
        oauthAccessTokenService.createToken(
            oauthAccessToken.getUser().getId(), refreshTokenId, oauthAccessToken.getProvider());
    return oauthAccessTokenMapper.toOauthAccessTokenResponse(
        oauthAccessToken,
        this.createAccessToken(result),
        refreshToken,
        appProperties.getAuth().getTokenExpirationMsec() / 1000);
  }

  private String createAccessToken(OauthAccessToken oauthAccessToken) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

    return Jwts.builder()
        .setSubject(oauthAccessToken.getId().toString())
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
        .compact();
  }

  private String createRefreshToken(OauthAccessToken oauthAccessToken) {
    Date now = new Date();
    Date expiryDate =
        new Date(now.getTime() + appProperties.getAuth().getRefreshTokenExpirationMsec());

    return Jwts.builder()
        .setSubject(oauthAccessToken.getRefreshToken().toString())
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getRefreshTokenSecret())
        .compact();
  }

  public User getUserFromToken(String token) {
    return this.getOauthAccessTokenFromToken(token).getUser();
  }

  public OauthAccessToken getOauthAccessTokenFromToken(String token) {
    this.validateAccessToken(token, appProperties.getAuth().getTokenSecret());
    OauthAccessToken oauthAccessToken =
        oauthAccessTokenService.getOauthAccessTokenById(
            this.getUUIDFromToken(token, appProperties.getAuth().getTokenSecret()));
    if (oauthAccessToken.getRevokedAt() != null) {
      throw new ForbiddenException(MessageConstant.REVOKED_TOKEN);
    }
    return oauthAccessToken;
  }

  private UUID getUUIDFromToken(String token, String secret) {
    Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    return UUID.fromString(claims.getSubject());
  }

  private void validateAccessToken(String authToken, String secret) {
    try {
      Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
    } catch (ExpiredJwtException ex) {
      throw new ForbiddenException(MessageConstant.EXPIRED_TOKEN);
    } catch (Exception ex) {
      throw new ForbiddenException(MessageConstant.INVALID_TOKEN);
    }
  }

  private void validateRefreshToken(String authToken, String secret) {
    try {
      Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
    } catch (ExpiredJwtException ex) {
      throw new ForbiddenException(MessageConstant.EXPIRED_REFRESH_TOKEN);
    } catch (Exception ex) {
      throw new ForbiddenException(MessageConstant.INVALID_REFRESH_TOKEN);
    }
  }
}
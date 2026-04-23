error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/security/JwtTokenProvider.java:io/jsonwebtoken/Claims#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/security/JwtTokenProvider.java
empty definition using pc, found symbol in pc: io/jsonwebtoken/Claims#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 68
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/security/JwtTokenProvider.java
text:
```scala
package org.unimate.unimate.api.security;

import io.jsonwebtoken.@@Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.unimate.unimate.domain.entities.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

  private static final String CLAIM_TYPE = "type";
  private static final String ACCESS_TOKEN_TYPE = "access";
  private static final String REFRESH_TOKEN_TYPE = "refresh";
  private static final String USER_ID_CLAIM = "userId";
  private static final String USER_ROLE_CLAIM = "role";
  private static final String USER_EMAIL_CLAIM = "email";
  private static final int MIN_SECRET_LENGTH = 32;

  private final String jwtSecret;
  private final long accessTokenExpirationMillis;
  private final long refreshTokenExpirationMillis;
  private SecretKey signingKey;

  public JwtTokenProvider(
      @Value("${jwt.secret:${JWT_SECRET_KEY:VMBx/sz10oVRGMWDloQ6rZTokIVsXDo4xv+nXFNk9oA=}}") String jwtSecret,
      @Value("${jwt.expiration:3600000}") long accessTokenExpirationMillis,
      @Value("${jwt.refresh-expiration:604800000}") long refreshTokenExpirationMillis
  ) {
    this.jwtSecret = jwtSecret;
    this.accessTokenExpirationMillis = accessTokenExpirationMillis;
    this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
  }

  @PostConstruct
  void init() {
    if (jwtSecret == null || jwtSecret.isEmpty() || jwtSecret.length() < MIN_SECRET_LENGTH) {
      throw new IllegalStateException(
          "JWT secret is invalid. Configure JWT_SECRET_KEY with at least " + MIN_SECRET_LENGTH + " characters."
      );
    }
    signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    log.info("JwtTokenProvider initialized with secret length: {}", jwtSecret.length());
  }

  public String generateToken(User user) {
    return buildToken(user, ACCESS_TOKEN_TYPE, accessTokenExpirationMillis);
  }

  public String generateRefreshToken(User user) {
    return buildToken(user, REFRESH_TOKEN_TYPE, refreshTokenExpirationMillis);
  }

  public boolean validateToken(String token) {
    try {
      if (token == null || token.isEmpty()) {
        log.debug("Token is null or empty");
        return false;
      }
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException ex) {
      log.debug("Token validation failed: {}", ex.getMessage());
      return false;
    }
  }

  public Integer getUserIdFromToken(String token) {
    try {
      Object uid = parseClaims(token).get(USER_ID_CLAIM);
      return uid instanceof Integer ? (Integer) uid : ((Number) uid).intValue();
    } catch (Exception ex) {
      log.debug("Failed to extract user ID from token: {}", ex.getMessage());
      return null;
    }
  }

  public String getUserEmailFromToken(String token) {
    try {
      return parseClaims(token).get(USER_EMAIL_CLAIM, String.class);
    } catch (Exception ex) {
      log.debug("Failed to extract email from token: {}", ex.getMessage());
      return null;
    }
  }

  public String getUserRoleFromToken(String token) {
    try {
      return parseClaims(token).get(USER_ROLE_CLAIM, String.class);
    } catch (Exception ex) {
      log.debug("Failed to extract role from token: {}", ex.getMessage());
      return null;
    }
  }

  public long getAccessTokenExpirationMillis() {
    return accessTokenExpirationMillis;
  }

  public long getRefreshTokenExpirationMillis() {
    return refreshTokenExpirationMillis;
  }

  private String buildToken(User user, String tokenType, long expirationMillis) {
    Instant now = Instant.now();
    Instant expiry = now.plusMillis(expirationMillis);

    try {
      return Jwts.builder()
          .subject(String.valueOf(user.getId()))
          .claim(USER_ID_CLAIM, user.getId())
          .claim(USER_EMAIL_CLAIM, user.getEmail())
          .claim(USER_ROLE_CLAIM, user.getRole().name())
          .claim(CLAIM_TYPE, tokenType)
          .issuedAt(Date.from(now))
          .expiration(Date.from(expiry))
          .signWith(signingKey)
          .compact();
    } catch (Exception ex) {
      log.error("Failed to build JWT token: {}", ex.getMessage());
      throw new RuntimeException("Failed to generate token", ex);
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: io/jsonwebtoken/Claims#
error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/security_example/AuthenticationService.java:_empty_/log#
file:///F:/uni/projects/upb-wp-project/unimate_backend/security_example/AuthenticationService.java
empty definition using pc, found symbol in pc: _empty_/log#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1239
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/security_example/AuthenticationService.java
text:
```scala
package com.autopartner.api.auth;

import com.autopartner.api.dto.request.AuthenticationRequest;
import com.autopartner.api.dto.request.RefreshTokenRequest;
import com.autopartner.api.dto.response.AuthenticationResponse;
import com.autopartner.api.dto.response.UserResponse;
import com.autopartner.domain.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

  AuthenticationManager authenticationManager;
  JwtVerifier tokenService;

  @Transactional
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    @@log.info("Authentication request for email: {}", request.getEmail());
    
    // Verify company and email confirmation before authentication
    log.debug("Verifying company for user: {}", request.getEmail());
    tokenService.verifyCompany(request.getEmail());
    
    log.debug("Verifying email confirmation for user: {}", request.getEmail());
    tokenService.verifyEmailConfirmation(request.getEmail());
    
    // Authenticate user
    log.debug("Authenticating user: {}", request.getEmail());
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User user = (User) authentication.getPrincipal();
    log.debug("User authenticated successfully: email={}, id={}", user.getEmail(), user.getId());
    
    // Generate tokens
    log.debug("Generating access and refresh tokens");
    String token = tokenService.generateToken(user.getEmail());
    String refreshToken = tokenService.generateRefreshToken(user.getEmail(), true);
    
    log.info("Authentication successful for user: {}", user.getEmail());
    return new AuthenticationResponse(token, refreshToken, UserResponse.fromEntity(user));
  }

  @Transactional
  public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
    log.info("Refresh token request received");
    String requestRefreshToken = request.getRefreshToken();
    
    if (requestRefreshToken == null || requestRefreshToken.isBlank()) {
      log.warn("Refresh token request missing or empty");
      throw new AuthenticationException("Refresh token is required") {};
    }
    
    log.debug("Verifying refresh token");
    String tokenId = tokenService.verifyRefreshToken(requestRefreshToken);

    if (tokenId == null) {
      log.warn("Refresh token verification failed - token is invalid, expired, or malformed");
      throw new AuthenticationException("Invalid refresh token") {};
    }
    
    log.debug("Refresh token verified successfully, tokenId: {}", tokenId);

    log.debug("Disabling refresh token and retrieving user");
    User user = tokenService.disableToken(tokenId);
    log.debug("User retrieved: email={}, id={}, active={}, verified={}", 
        user.getEmail(), user.getId(), user.getActive(), user.getVerified());

    // Verify user status before issuing new tokens
    log.debug("Verifying company for user: {}", user.getEmail());
    try {
      tokenService.verifyCompany(user.getEmail());
      log.debug("Company verification successful");
    } catch (AuthenticationException e) {
      log.warn("Company verification failed for user {}: {}", user.getEmail(), e.getMessage());
      throw e;
    }
    
    log.debug("Verifying email confirmation for user: {}", user.getEmail());
    try {
      tokenService.verifyEmailConfirmation(user.getEmail());
      log.debug("Email confirmation verification successful");
    } catch (Exception e) {
      log.warn("Email confirmation verification failed for user {}: {}", user.getEmail(), e.getMessage());
      throw e;
    }

    log.debug("Generating new access and refresh tokens");
    String token = tokenService.generateToken(user.getEmail());
    String refreshToken = tokenService.generateRefreshToken(user.getEmail(), false);
    log.info("Token refresh successful for user: {}", user.getEmail());
    return new AuthenticationResponse(token, refreshToken, UserResponse.fromEntity(user));
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/log#
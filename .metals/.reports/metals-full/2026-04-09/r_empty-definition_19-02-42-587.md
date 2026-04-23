error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/security_example/AuthenticationController.java:_empty_/log#
file:///F:/uni/projects/upb-wp-project/unimate_backend/security_example/AuthenticationController.java
empty definition using pc, found symbol in pc: _empty_/log#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1854
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/security_example/AuthenticationController.java
text:
```scala
package com.autopartner.api.auth;

import com.autopartner.api.dto.request.AuthenticationRequest;
import com.autopartner.api.dto.request.PasswordRequest;
import com.autopartner.api.dto.request.RefreshTokenRequest;
import com.autopartner.api.dto.response.AuthenticationResponse;
import com.autopartner.api.dto.response.ClientUserActivationResponse;
import com.autopartner.api.dto.response.UserResponse;
import com.autopartner.domain.User;
import com.autopartner.service.ActivationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

  ActivationService activationService;
  AuthenticationService authenticationService;

  @PostMapping
  public AuthenticationResponse auth(
      @RequestBody @Valid AuthenticationRequest authenticationRequest)
      throws AuthenticationException {
    return authenticationService.authenticate(authenticationRequest);
  }

  @PostMapping("/refresh")
  public AuthenticationResponse refresh(@RequestBody RefreshTokenRequest request) {
    return authenticationService.refreshToken(request);
  }

  @GetMapping("/activation-info")
  public ClientUserActivationResponse getActivationInfo(
      @RequestParam String token) {
    return activationService.getActivationInfo(token);
  }

  @PostMapping("/activate")
  public void activateAccount(@RequestParam String token, @RequestBody PasswordRequest request) {
    @@log.info("Activating account with token: {}", token);
    activationService.activateAccount(token, request);
  }

  @GetMapping("/current-user")
  public UserResponse getCurrentUser(@AuthenticationPrincipal User user){
    return UserResponse.fromEntity(user);
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/log#
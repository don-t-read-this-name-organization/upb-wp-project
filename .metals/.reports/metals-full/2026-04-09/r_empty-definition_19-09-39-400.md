error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/controller/AuthController.java:_empty_/`<any>`#map#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/controller/AuthController.java
empty definition using pc, found symbol in pc: _empty_/`<any>`#map#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 3233
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/controller/AuthController.java
text:
```scala
package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.auth.AuthResponse;
import org.unimate.unimate.api.dto.auth.LoginRequest;
import org.unimate.unimate.api.dto.user.response.UserResponse;
import org.unimate.unimate.api.security.JwtTokenProvider;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.service.UserService;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthController {
    UserService userService;
    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (!isValid(request.getEmail()) || !isValid(request.getPassword())) {
            return error(HttpStatus.BAD_REQUEST, "Email and password are required");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            User user = (User) auth.getPrincipal();

            if (!user.getActive()) {
                return error(HttpStatus.UNAUTHORIZED, "User account is inactive");
            }

            AuthResponse response = AuthResponse.of(
                UserResponse.fromEntity(user),
                jwtTokenProvider.generateToken(user),
                jwtTokenProvider.generateRefreshToken(user)
            );
            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return error(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        } catch (Exception ex) {
            log.error("Login error: {}", ex.getMessage(), ex);
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String token = request.get("refreshToken");
        if (!isValid(token)) {
            return error(HttpStatus.BAD_REQUEST, "Refresh token is required");
        }

        if (!jwtTokenProvider.validateToken(token)) {
            return error(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        Integer userId = jwtTokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            return error(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        return userService.findById(userId)
            .filter(User::getActive)
            .@@map(user -> ResponseEntity.ok(Map.of(
                "accessToken", jwtTokenProvider.generateToken(user),
                "refreshToken", token,
                "token", jwtTokenProvider.generateToken(user)
            )))
            .orElseGet(() -> error(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    private boolean isValid(String value) {
        return value != null && !value.isBlank();
    }

    private ResponseEntity<?> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/`<any>`#map#
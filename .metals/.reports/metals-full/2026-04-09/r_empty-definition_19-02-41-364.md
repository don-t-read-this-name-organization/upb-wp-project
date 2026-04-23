error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/controller/AuthController.java:java/util/Map#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/controller/AuthController.java
empty definition using pc, found symbol in pc: java/util/Map#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 931
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

import java.util.@@Map;

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
        log.info("Login attempt for email: {}", request.getEmail());

        if (request.getEmail() == null || request.getEmail().isEmpty() || 
            request.getPassword() == null || request.getPassword().isEmpty()) {
            log.warn("Login attempt with missing email or password");
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Email and password are required"));
        }

        try {
            // Authenticate using Spring's AuthenticationManager
            // This will use CustomUserDetailsService to load user and BCryptPasswordEncoder to verify password
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Get authenticated user
            User user = (User) authentication.getPrincipal();

            // Check if user is active
            if (!Boolean.TRUE.equals(user.getActive())) {
                log.warn("Login attempt for inactive user: {}", user.getEmail());
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User account is inactive"));
            }

            // Generate tokens
            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            
            AuthResponse response = AuthResponse.of(
                UserResponse.fromEntity(user),
                accessToken,
                refreshToken
            );
            
            log.info("Successful login for user: {}", user.getEmail());
            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for email: {}: {}", request.getEmail(), ex.getMessage());
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password"));
        } catch (Exception ex) {
            log.error("Error during login: {}", ex.getMessage(), ex);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An error occurred during authentication"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        log.info("Token refresh attempt");

        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Refresh token is required"));
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Invalid refresh token");
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid refresh token"));
        }

        try {
            Integer userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            if (userId == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
            }

            return userService.findById(userId)
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .map(user -> {
                    String newAccessToken = jwtTokenProvider.generateToken(user);
                    log.info("Token refreshed for user: {}", user.getEmail());
                    return ResponseEntity.ok(Map.of(
                        "accessToken", newAccessToken,
                        "refreshToken", refreshToken,
                        "token", newAccessToken // Legacy
                    ));
                })
                .orElseGet(() -> {
                    log.warn("User not found for token refresh");
                    return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
                });
        } catch (Exception ex) {
            log.error("Error during token refresh: {}", ex.getMessage(), ex);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Logout requested");
        // JWT tokens are stateless, so logout is handled client-side by removing tokens
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/Map#